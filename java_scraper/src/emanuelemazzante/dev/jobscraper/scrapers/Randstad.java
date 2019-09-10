package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;

/**
 *
 * @author Emanuele
 */
public class Randstad extends ScraperBase {

    private Randstad(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Randstad randstad = new Randstad(sourceId, region);
        randstad.start();
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

    public void load() throws Exception {
        String url = "https://www.randstad.it/offerte-lavoro/" + region + "/";
        Document doc = getDocument(url);
        String jobCountString = doc.select(".job-search-header-jobs > span").first().text();
        int jobCount = 0;
        try {
            jobCount = Integer.parseInt(jobCountString.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
        }
        if (jobCount > 0) {
            //pagina iniziale
            System.out.println("Scraping pagina inizale");
            scrapeJob(doc);
            //calcolo totale pagine
            int pages;
            int currentPage = 2;
            if (jobCount % 10 == 0) {
                pages = jobCount / 10;
            } else {
                pages = jobCount / 10 + 1;
            }
            System.out.println("Totale pagine: " + pages);
            //scraping pagine seguenti
            while (currentPage <= pages) {
                System.out.println("Scraping pagina " + currentPage);
                url = "https://www.randstad.it/offerte-lavoro/" + region + "/page-" + currentPage++;
                doc = getDocument(url);
                scrapeJob(doc);
            }
        }
    }

    private void scrapeJob(Document doc) throws Exception {
        Elements jobs = doc.select("article");
        for (Element job : jobs) {
            String title = job.selectFirst("header > h2").text();
            Element timeElement = job.selectFirst("time");
            String publishedAtString = timeElement.text();
            Date publishedAtDate = Date.valueOf(timeElement.attr("datetime"));
            String place = job.selectFirst(".job-summary-small-location").text();
            String contentUrl = "https://www.randstad.it" + job.select("a").last().attr("href");
            String jobId = parseIdFromLink(contentUrl);
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
            } else {
                String content = getContent(contentUrl);
                JobPost jobPost = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, null, title, place, content, region, contentUrl);
                DbUtils.saveJobPost(jobPost);
                this.jobAdded++;
            }
        }
    }

    private String getContent(String url) throws Exception {
        Document doc = getDocument(url);
        return doc.selectFirst(".job-details-desc-wrapper-inner").text();
    }

    private String parseIdFromLink(String link) throws Exception {
        boolean continueBackward = true;
        int startIndex = link.length() - 2;
        while (continueBackward) {
            if (Character.isDigit(link.charAt(startIndex))) {
                startIndex--;
            } else {
                startIndex++;
                continueBackward = false;
            }
        }
        return link.substring(startIndex).replace("/", "");
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.randstad.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "www.randstad.it");
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
}
