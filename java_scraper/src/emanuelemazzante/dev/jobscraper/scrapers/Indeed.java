package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import emanuelemazzante.dev.jobscraper.util.Utils;
import java.util.Calendar;

/**
 *
 * @author emanuelemazzante
 */
public class Indeed extends ScraperBase {

    private int POSTS_PER_PAGE;
    private String SORT_BY;
    private int RADIUS;
    private int POSTS_TO_SCRAPE;

    private Indeed(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Indeed indeed = new Indeed(sourceId, region);
        if (indeed.readSettings()) {
            indeed.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/indeed.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "RADIUS":
                            this.RADIUS = Integer.parseInt(settings[1].trim());
                            break;
                        case "POSTS_TO_SCRAPE":
                            this.POSTS_TO_SCRAPE = Integer.parseInt(settings[1].trim());
                            break;
                        case "POSTS_PER_PAGE":
                            this.POSTS_PER_PAGE = Integer.parseInt(settings[1].trim());
                            break;
                        case "SORT_BY":
                            this.SORT_BY = settings[1].trim();
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

    public void start() {
        ScraperReport report;
        try {
            System.out.println("\nINIZIO SCRAPER " + sourceId + " | REGIONE: " + region + "\n");
            int page_count = 1;
            int post_start = POSTS_PER_PAGE;
            while (post_start <= POSTS_TO_SCRAPE) {
                System.out.println("scraping pagina n. " + page_count++);
                Document pageContent = loadPage(post_start);
                scrapePosts(pageContent);
                post_start += POSTS_PER_PAGE;
            }
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            e.printStackTrace();
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    private Document loadPage(int start) throws Exception {
        String url;
        if (start == POSTS_PER_PAGE) {
            url = "https://it.indeed.com/offerte-lavoro?" + "radius=" + RADIUS + "&l=" + region + "&fromage=any&limit=" + POSTS_PER_PAGE + "&sort=" + SORT_BY + "&psf=advsrch";
        } else {
            url = "https://it.indeed.com/jobs?" + "radius=" + RADIUS + "&l=" + region + "&fromage=any&limit=" + POSTS_PER_PAGE + "&sort=" + SORT_BY + "&start=" + start + "&psf=advsrch";
        }
        return getDocument(url);
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "it.indeed.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "https://it.indeed.com/advanced_search?q=&l=" + region);
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
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
        return doc;
    }

    private Document getPostContent(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "it.indeed.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "https://it.indeed.com/advanced_search?q=&l=" + region);
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        //con.setInstanceFollowRedirects(false);
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
        if (responseCode == 302) {
            String redirectLink = doc.selectFirst("a").attr("href");
            if (redirectLink.startsWith("http:")) {
                redirectLink = redirectLink.replace("http:", "https:");
            }
            return getPostContent(redirectLink);
        } else {
            return doc;
        }
    }

    private String loadContent(String url) throws Exception {
        Document doc = getPostContent(url);
        Element content = doc.getElementById("jobDescriptionText");
        if (content != null) {
            return content.text();
        }
        return doc.getElementById("job-description").text();
    }

    private void scrapePosts(Document doc) throws Exception {
        Element table = doc.getElementById("resultsCol");
        Elements jobs = table.select(".jobsearch-SerpJobCard");
        for (Element job : jobs) {
            parseJobInfo(job);
        }
    }

    private void parseJobInfo(Element job) throws Exception {
        String jobId = job.selectFirst(".title > a").attr("id");
        if (jobId.startsWith("jl_")) {
            String link = "https://it.indeed.com" + job.selectFirst(".title > a").attr("href");
            jobId = jobId.replace("jl_", "");
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                this.jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
                return;
            }
            String title = job.selectFirst(".title > a").text();
            String companyName = job.selectFirst(".company").text();
            String place = job.selectFirst(".location").text();
            String publishedAt = job.selectFirst("span.date").text();
            java.sql.Date publishedAtDate = parseDate(publishedAt.toLowerCase());
            String content = loadContent(link);
            JobPost jobPost = new JobPost(sourceId, jobId, publishedAt, publishedAtDate, companyName, title, place, content, region, link);
            DbUtils.saveJobPost(jobPost);
            this.jobAdded++;
        }
    }

    private java.sql.Date parseDate(String stringDate) {
        if (stringDate.contains("adesso") || stringDate.contains("oggi")) {
            return new java.sql.Date(new java.util.Date().getTime());
        }
        if (stringDate.contains("giorn") && stringDate.contains("fa")) {
            stringDate = stringDate.replaceAll("\\D+", "");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new java.util.Date());
            cal.add(Calendar.DATE, -(Integer.parseInt(stringDate)));
            return new java.sql.Date(cal.getTimeInMillis());
        }
        return null;
    }
}
