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
import java.sql.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author emanuelemazzante
 */
public class Linkedin extends ScraperBase {

    private int JOBS_TO_SCRAPE;

    private Linkedin(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Linkedin linkedin = new Linkedin(sourceId, region);
        if (linkedin.readSettings()) {
            linkedin.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/linkedin.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "JOBS_TO_SCRAPE":
                            this.JOBS_TO_SCRAPE = Integer.parseInt(settings[1].trim());
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
        int start = 0;
        while (start < JOBS_TO_SCRAPE) {
            System.out.println("Scraping annunci dal numero " + start);
            loadPage(start);
            start += 25;
        }
    }

    private void loadPage(int start) throws Exception {
        URL obj = new URL("https://www.linkedin.com/jobs-guest/jobs/api/jobPostings/jobs?location=marche&redirect=false&position=1&pageNum=0&sortBy=DD&start=" + start);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.linkedin.com");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        System.out.println(responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        Document doc = Jsoup.parse(response.toString());
        scrapeJobsInCurrentPage(doc);
    }

    private void scrapeJobsInCurrentPage(Document doc) throws Exception {
        Elements jobs = doc.select("li");
        for (Element job : jobs) {
            String jobId = job.attr("data-id");
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
            } else {
                String title = job.selectFirst("h3.result-card__title").text();
                String place = job.selectFirst("span.job-result-card__location").text();
                Element timeElement = job.selectFirst("time");
                String publishedAtString = timeElement.text();
                Date publishedAtDate = Date.valueOf(timeElement.attr("datetime"));
                String contentUrl = job.selectFirst("a.result-card__full-card-link").attr("href");
                String company = job.selectFirst("h4.result-card__subtitle").text();
                String content = job.selectFirst("p.job-result-card__snippet").text();
                JobPost jobPost = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, company, title, place, content, region, contentUrl);
                DbUtils.saveJobPost(jobPost);
                this.jobAdded++;
            }
        }
    }
}
