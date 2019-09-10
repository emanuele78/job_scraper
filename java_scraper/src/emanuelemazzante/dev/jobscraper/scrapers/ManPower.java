package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import emanuelemazzante.dev.jobscraper.util.Utils;

public class ManPower extends ScraperBase {

    private int JOBS_PER_PAGE;

    private ManPower(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        ManPower manpower = new ManPower(sourceId, region);
        if (manpower.readSettings()) {
            manpower.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/manpower.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "JOBS_PER_PAGE":
                            this.JOBS_PER_PAGE = Integer.parseInt(settings[1].trim());
                            break;
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            ScraperReport report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
            System.out.println(e);
            DbUtils.addReport(report);
            proceed = false;
        }
        return proceed;
    }

    public void start() {
        ScraperReport report;
        System.out.println("\nINIZIO SCRAPER " + sourceId + " | REGIONE: " + region + "\n");
        try {
            load();
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    private void load() throws Exception {
        //pagina iniziale
        String url = "https://www.manpower.it/trova-lavoro/" + region + "?q_dist=30&q_rpp=" + JOBS_PER_PAGE;
        Document doc = getDocument(url);
        Element jobsStringCount = doc.selectFirst(".listing-seo-title > b");
        int jobCount = Integer.parseInt(jobsStringCount.text());
        System.out.println("Annunci trovati " + jobCount);
        int pages;
        if (jobCount % JOBS_PER_PAGE == 0) {
            pages = jobCount / JOBS_PER_PAGE;
        } else {
            pages = jobCount / JOBS_PER_PAGE + 1;
        }
        System.out.println("Totale pagine: " + pages);
        //scraping annunci prima pagina
        System.out.println("Scraping pagina 1");
        parsePage(doc);
        //scraping pagine successive
        int currentPages = 2;
        if (pages > 1) {
            while (currentPages < pages) {
                System.out.println("Scraping pagina " + currentPages);
                url = "https://www.manpower.it/trova-lavoro/" + region + "/" + currentPages++ + "?q_dist=30&q_rpp=" + JOBS_PER_PAGE;
                doc = getDocument(url);
                parsePage(doc);
            }
        }
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.manpower.it");
        con.setRequestProperty("Connection", "keep-alive");
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

    private void parsePage(Document doc) throws Exception {
        Elements jobs = doc.select(".qrow.ad-container");
        for (Element job : jobs) {
            String title = job.selectFirst("h3[itemprop=title]").text();
            String contentUrl = job.selectFirst("a").attr("href");
            String publishedAtString = job.selectFirst("span[itemprop=datePosted]").text();
            String contentUrlSplitted[] = contentUrl.split("/");
            String jobId = contentUrlSplitted[contentUrlSplitted.length - 1];
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
            } else {
                String contents[] = scrapeContent(contentUrl);
                Date publishedAtDate;
                try {
                    java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(publishedAtString);
                    publishedAtDate = new Date(utilDate.getTime());
                } catch (Exception e) {
                    publishedAtDate = null;
                }
                JobPost jobPost = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, null, title, contents[1], contents[0], region, contentUrl);
                DbUtils.saveJobPost(jobPost);
                this.jobAdded++;
            }
        }
    }

    private String[] scrapeContent(String url) throws Exception {
        Document doc = getDocument(url);
        String content[] = new String[2];
        content[0] = doc.selectFirst(".ad-info-box").text();
        content[1] = doc.selectFirst("span[itemprop=addressLocality]").text();
        return content;
    }
}
