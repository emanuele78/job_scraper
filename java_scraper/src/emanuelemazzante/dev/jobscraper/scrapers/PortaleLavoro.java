/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emanuelemazzante.dev.jobscraper.scrapers;

import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author emanuelemazzante
 */
public class PortaleLavoro extends ScraperBase {

    private int SLEEP_LENGTH;

    public PortaleLavoro(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        PortaleLavoro portaleLavoro = new PortaleLavoro(sourceId, region);
        if (portaleLavoro.readSettings()) {
            portaleLavoro.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/portalelavoro.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "SLEEP":
                            this.SLEEP_LENGTH = Integer.parseInt(settings[1].trim());
                            break;
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            ScraperReport report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
            DbUtils.addReport(report);
            proceed = false;
        }
        return proceed;
    }

    private void start() {
        ScraperReport report;
        System.out.println("\nINIZIO SCRAPER " + sourceId + " | REGIONE: " + region + "\n");
        try {
            load();
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            e.printStackTrace();
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    private void load() throws Exception {
        String url = "https://www.portalelavoro.com/" + region + "/r32.html";
        Document doc = getDocument(url);
        String lastPageDescription = doc.selectFirst("div.pagineRimanenti").text();
        int pages = Integer.parseInt(lastPageDescription.replace("Pagina 1 di ", "").trim());
        System.out.println("Totale pagine: " + pages);
        System.out.println("Scraping pagina 1");
        //pagina iniziale
        parseJobs(doc);
        //pagine successive
        int currentPage = 2;
        if (pages > 1) {
            while (currentPage <= pages) {
                Thread.sleep(this.SLEEP_LENGTH);
                System.out.println("Scraping pagina " + currentPage);
                url = "https://www.portalelavoro.com/" + region + "/r32.html?page=" + currentPage++;
                doc = getDocument(url);
                parseJobs(doc);
            }
        }
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.portalelavoro.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        return Jsoup.parse(response.toString());
    }

    private void parseJobs(Document doc) throws Exception {
        Elements jobs = doc.select(".contentItem div.dettaglioAnnuncio");
        for (Element job : jobs) {
            String contentUrl = job.selectFirst("a").attr("href");
            parseJobContent(job, contentUrl);
        }
    }

    private void parseJobContent(Element job, String link) throws Exception {
        String title = job.selectFirst("h3").text();
        String publishedAt = job.selectFirst(".annuncioData").text();
        String company = job.selectFirst(".annuncioSettore").text();
        Element placeElement = job.selectFirst("span[itemprop=addressLocality]");
        String place = null;
        if (placeElement != null) {
            place = job.selectFirst("span[itemprop=addressLocality]").text();
        }
        String content = job.selectFirst("div.annuncioDescrizione").text();
        java.sql.Date publishedAtDate = parseDate(publishedAt.toLowerCase());
        String jobId = createJobIdFromTitle(title, publishedAt);
        if (DbUtils.jobExists(jobId, sourceId, region)) {
            jobSkipped++;
            System.out.println("annuncio saltato perché già presente");
            return;
        }
        JobPost jobPost = new JobPost(sourceId, jobId, publishedAt, publishedAtDate, company, title, place, content, region, link);
        DbUtils.saveJobPost(jobPost);
        this.jobAdded++;
    }

    private String createJobIdFromTitle(String title, String date) {
        int maxLengthAllowed = 100;
        String jobId = title.replace(" ", "").toLowerCase();
        if (jobId.length() > maxLengthAllowed) {
            return jobId.substring(0, maxLengthAllowed) + date;
        }
        return jobId.substring(0, jobId.length() - 1) + date;
    }

    private java.sql.Date parseDate(String stringDate) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date parsed = format.parse(stringDate);
        return new java.sql.Date(parsed.getTime());
    }

}
