package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

/**
 *
 * @author emanuelemazzante
 */
public class GiGroup extends ScraperBase {

    private static final String SOURCE = "GIGROUP";
    private static final String REGION = "MAR";
    private int OFFSET;
    private int POSTS_TO_SCRAPE;

    private GiGroup(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        GiGroup giGroup = new GiGroup(sourceId, region);
        if (giGroup.readSettings()) {
            giGroup.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/gigroup.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "OFFSET":
                            this.OFFSET = Integer.parseInt(settings[1].trim());
                            break;
                        case "POSTS_TO_SCRAPE":
                            this.POSTS_TO_SCRAPE = Integer.parseInt(settings[1].trim());
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
        System.out.println("\nINIZIO SCRAPER " + SOURCE + " | REGIONE: " + region + "\n");
        ScraperReport report;
        try {
            loadJobs();
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    public void loadJobs() throws Exception {
        String rawData = "X_GUMM_REQUESTED_WITH=XMLHttpRequest&action=scrollpagination&numberLimit=" + POSTS_TO_SCRAPE + "&offset=" + OFFSET;
        String url = "https://www.gigroup.it/wp-content/themes/gi-group-child/candidati-ricerche-attive-1-offerte.php?re=" + REGION;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "www.gigroup.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Origin", "https://www.gigroup.it");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Referer", "https://www.gigroup.it/offerte-lavoro/?ap=&si=&re=" + REGION + "&pr=&ts=&tp=&offerta=&dove=");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setConnectTimeout(60000);
        // Send post request
        con.setDoOutput(true);
        OutputStreamWriter w = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
        w.write(rawData);
        w.close();
        //reading response
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        Document doc = Jsoup.parse(response.toString());
        scrapePost(doc);
    }

    public void scrapePost(Document document) throws Exception {
        Elements results = document.select("article.lavoroRiga");
        for (Element job : results) {
            parseJobInfo(job);
        }
    }

    public void parseJobInfo(Element job) throws Exception {
        String title = job.selectFirst("a.elencoOfferte > h2").text();
        String place = job.select(".lavoroCol2 > h3").last().text();
        Elements divElements = job.select(".lavoroCol2");
        String publishedAtString = divElements.get(divElements.size() - 1).text();
        Date publishedAtDate;
        try {
            java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(publishedAtString);
            publishedAtDate = new Date(utilDate.getTime());
        } catch (Exception e) {
            publishedAtDate = null;
        }
        String contentUrl = "https://www.gigroup.it" + job.selectFirst("a").attr("href");
        String jobId = contentUrl.substring(contentUrl.indexOf("=") + 1);
        if (DbUtils.jobExists(jobId, sourceId, region)) {
            jobSkipped++;
            System.out.println("annuncio saltato perché già presente");
            return;
        }
        String content = getJobPostContent(contentUrl);
        if (content == null) {
            System.out.println("Impossibile accedere al contenuto dell'annuncio");
            this.jobSkipped++;
        } else {
            JobPost jobPost = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, null, title, place, content, region, contentUrl);
            DbUtils.saveJobPost(jobPost);
            this.jobAdded++;
        }
    }

    public String getJobPostContent(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("GET");
            con.setRequestProperty("Host", "www.gigroup.it");
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
            Document doc = Jsoup.parse(response.toString());
            return doc.getElementById("JobDescriptionID").text();
        } catch (Exception e) {
            return null;
        }
    }
}
