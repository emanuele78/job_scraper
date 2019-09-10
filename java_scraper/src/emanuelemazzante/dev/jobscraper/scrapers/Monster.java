package emanuelemazzante.dev.jobscraper.scrapers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import emanuelemazzante.dev.jobscraper.util.Utils;
import java.util.Calendar;

/**
 *
 * @author emanuelemazzante
 */
public class Monster extends ScraperBase {

    private int START_PAGE;
    private int END_PAGE;

    private Monster(int sourceId, String region)  {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Monster monster = new Monster(sourceId, region);
        if (monster.readSettings()) {
            monster.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/monster.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "START_PAGE":
                            this.START_PAGE = Integer.parseInt(settings[1].trim());
                            break;
                        case "END_PAGE":
                            this.END_PAGE = Integer.parseInt(settings[1].trim());
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
        try {
            System.out.println("\nINIZIO SCRAPER " + sourceId + " | REGIONE: " + region + "\n");
            loadPage();
            System.out.println("Annunci inseriti: " + jobAdded);
            System.out.println("Annunci saltati perché già in memoria: " + jobSkipped);
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    public void loadPage() throws Exception {
        String url = "https://www.monster.it/lavoro/cerca/?where=Regione__3A" + region + "&stpage=" + START_PAGE + "&page=" + END_PAGE;
        Document doc = getDocument(url);
        scrapePost(doc);
    }

    private Document getDocument(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.monster.it");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        //on.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
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

    public void scrapePost(Document document) throws Exception {
        Element results = document.getElementById("SearchResults");
        Elements jobs = results.select("section");
        for (Element job : jobs) {
            Element adv = job.selectFirst("p.entry");
            //annuncio non sponsorizzato
            if (adv == null) {
                Element titleElement = job.selectFirst("h2.title");
                if (titleElement != null) {
                    parseJobInfo(job);
                }
            }
        }
    }

    public void parseJobInfo(Element jobPost) throws Exception {
        String jobId = jobPost.attr("data-jobid");
        if (DbUtils.jobExists(jobId, sourceId, region)) {
            jobSkipped++;
            System.out.println("annuncio saltato perché già presente");
            return;
        }
        Element titleElement = jobPost.selectFirst("h2.title");
        String title = titleElement.text();
        String jobUrl = titleElement.selectFirst("a").attr("href");
        Element companyElement = jobPost.selectFirst("div.company > a");
        if (companyElement == null) {
            companyElement = jobPost.selectFirst("div.company > span");
        }
        String companyName = companyElement.text();
        Element locationElement = jobPost.selectFirst("div.location > a");
        String place = null;
        if (locationElement == null) {
            locationElement = jobPost.selectFirst("div.location > span");
        }
        if (locationElement != null) {
            place = locationElement.text();
        }
        Element timeElement = jobPost.selectFirst("time");
        String publishedAtString = timeElement.text();
        //Date publishedAtDate = Date.valueOf(timeElement.attr("datetime").split("T")[0]);
        Date publishedAtDate = parseDate(publishedAtString);
        String content = getJobPostContent(jobUrl);
        if (content == null) {
            System.out.println("Annuncio non più disponibile");
            this.jobSkipped++;
        } else {
            JobPost job = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, companyName, title, place, content, region, jobUrl);
            boolean jobExists = DbUtils.jobExists(job.getJobId(), sourceId, region);
            if(jobExists){
                this.jobSkipped++;
            }else{
                DbUtils.saveJobPost(job);
                this.jobAdded++;
            }
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

    public String getJobPostContent(String url) {
        try {
            Document doc = getDocument(url);
            Element body = doc.getElementById("TrackingJobBody");
            return body.text();
        } catch (Exception e) {
            return null;
        }
    }
}
