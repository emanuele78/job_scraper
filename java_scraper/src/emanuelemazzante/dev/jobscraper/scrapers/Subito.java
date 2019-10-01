    package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import java.util.Calendar;

/**
 *
 * @author Emanuele
 */
public class Subito extends ScraperBase {

    private Subito(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Subito subito = new Subito(sourceId, region);
        subito.start();
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
        String url = "https://www.subito.it/annunci-" + region + "/vendita/offerte-lavoro/";
        Document doc = getDocument(url);
        String jobCountString = doc.selectFirst("p.total-ads").text();
        int jobCount = Integer.parseInt(jobCountString.replaceAll("\\D+", ""));
        int pages = getLastPage(doc);
        System.out.println("Annunci trovati: " + jobCount);
        System.out.println("Totale pagine: " + pages);
        System.out.println("Scraping pagina 1");
        //pagina iniziale
        parseJobs(doc);
        //pagine successive
        int currentPage = 2;
        if (pages > 1) {
            while (currentPage <= pages) {
                System.out.println("Scraping pagina " + currentPage);
                url = "https://www.subito.it/annunci-" + region + "/vendita/offerte-lavoro/?o=" + currentPage++;
                doc = getDocument(url);
                parseJobs(doc);
            }
        }
    }

    private int getLastPage(Document doc) throws Exception {
        Elements pageLinks = doc.select("div.pagination-container a");
        return Integer.parseInt(pageLinks.get(pageLinks.size() - 2).text());
    }

    private void parseJobs(Document doc) throws Exception {
        Elements jobs = doc.selectFirst("div.items.visible").select("a");
        for (Element job : jobs) {
            String contentUrl = job.attr("href");
            String urlSplitted[] = contentUrl.split("-");
            String jobId = urlSplitted[urlSplitted.length - 1].replace(".htm", "");
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
            } else {
                //new http request
                Document jobContent = getDocument(contentUrl);
                parseJobContent(jobContent, contentUrl);
            }
        }
    }

    private void parseJobContent(Document doc, String link) throws Exception {
        String jobId = doc.selectFirst("span.ad-info__id").text().replace("ID:", "").trim();
        String publishedAt = doc.selectFirst("span.ad-info__listing-time").text();
        String company = doc.selectFirst("p.user-name").text();
        String place = doc.selectFirst("span.ad-info__location__text").text();
        String content = doc.selectFirst("p.description").text();
        String title = doc.selectFirst("h1.ad-info__title").text();
        java.sql.Date publishedAtDate = parseDate(publishedAt.toLowerCase());
        JobPost jobPost = new JobPost(sourceId, jobId, publishedAt, publishedAtDate, company, title, place, content, region, link);
        DbUtils.saveJobPost(jobPost);
        this.jobAdded++;
    }

    private java.sql.Date parseDate(String stringDate) {
        try {
            if (stringDate.contains("adesso") || stringDate.contains("oggi")) {
                return new java.sql.Date(new java.util.Date().getTime());
            }
            if (stringDate.contains("ieri")) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new java.util.Date());
                cal.add(Calendar.DATE, -1);
                return new java.sql.Date(cal.getTimeInMillis());
            }
            int day = Integer.parseInt(stringDate.split(" ")[0].replaceAll("\\D+", ""));
            int month;
            if (stringDate.contains("gen")) {
                month = 0;
            } else if (stringDate.contains("feb")) {
                month = 1;
            } else if (stringDate.contains("mar")) {
                month = 2;
            } else if (stringDate.contains("apr")) {
                month = 3;
            } else if (stringDate.contains("mag")) {
                month = 4;
            } else if (stringDate.contains("giu")) {
                month = 5;
            } else if (stringDate.contains("lug")) {
                month = 6;
            } else if (stringDate.contains("ago")) {
                month = 7;
            } else if (stringDate.contains("set")) {
                month = 8;
            } else if (stringDate.contains("ott")) {
                month = 9;
            } else if (stringDate.contains("nov")) {
                month = 10;
            } else {
                month = 11;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new java.util.Date());
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            return new java.sql.Date(calendar.getTimeInMillis());
        } catch (Exception e) {
            return null;
        }
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.subito.it");
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
}
