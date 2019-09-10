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
import java.util.List;
import java.util.Map;
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
public class Adecco extends ScraperBase {

    private String session_cookies = "";
    private String USERNAME;
    private String PASSWORD;
    private final static int PROVINCIA_MC = 45;
    private final static int PROVINCIA_PU = 43;
    private final static int PROVINCIA_AN = 44;
    private final static int PROVINCIA_FM = 22352;
    private final static int PROVINCIA_AP = 46;
    private final static int[] PROVINCIE = {PROVINCIA_AN, PROVINCIA_AP, PROVINCIA_FM, PROVINCIA_MC, PROVINCIA_PU};

    private Adecco(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        Adecco adecco = new Adecco(sourceId, region);
        if (adecco.readSettings()) {
            adecco.start();
        }
    }

    private boolean readSettings() {
        boolean proceed = true;
        File file = new File(Utils.getExecutablePath() + "/settings/adecco.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "USERNAME":
                            this.USERNAME = settings[1].trim();
                            break;
                        case "PASSWORD":
                            this.PASSWORD = settings[1].trim();
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
            System.out.println("Acquisizione sessione");
            getSessionCookies();
            System.out.println("Login");
            doLogin();
            System.out.println("After login");
            afterLogin();
            System.out.println("Candidate desktop");
            boolean result = candidateDesktop();
            if (result) {
                for (int provincia : PROVINCIE) {
                    loadJobs(provincia);
                }
            }
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    public void getSessionCookies() throws Exception {
        String url = "https://candidate.adecco.it/candidate/login.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "candidate.adecco.it");
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
        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null) {
                if (entry.getKey().toLowerCase().equals("set-cookie")) {
                    List<String> list = entry.getValue();
                    for (String s : list) {
                        session_cookies += s.substring(0, s.indexOf(";") + 1) + " ";
                    }
                }
            }
        }
        in.close();
    }

    public void doLogin() throws Exception {
        String rawData = "x=&y=" + PASSWORD + "&z=" + USERNAME;
        String url = "https://candidate.adecco.it/candidate/ajax/login.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "candidate.adecco.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Origin", "https://candidate.adecco.it");
        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Referer", "https://candidate.adecco.it/candidate/login.asp");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", session_cookies);
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
    }

    public void afterLogin() throws Exception {
        String url = "https://candidate.adecco.it/candidate/forms/desktop.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "candidate.adecco.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", session_cookies);
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
    }

    public boolean candidateDesktop() throws Exception {
        String url = "https://candidate.adecco.it/candidate/desktop.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "candidate.adecco.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("Referer", "https://candidate.adecco.it/candidate/login.asp");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", session_cookies);
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println("Codice risposta " + responseCode + "\n");
        Document doc = Jsoup.parse(response.toString());
        Element jobs = doc.getElementById("lnkOffers");
        if (jobs != null) {
            System.out.println("Pannello controllo raggiunto");
            return true;
        } else {
            System.out.println("Errore nella procedura");
            return false;
        }
    }

    public void loadJobs(int codice_provincia) throws Exception {
        String rawData = "cmbProvince1=" + codice_provincia + "&Caller=searchoffer.asp&lSearchNumber=0&bCandidateRegister=true&bHasBeenModifiedProvince=true";
        String url = "https://candidate.adecco.it/candidate/reporting/searchoffer.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "candidate.adecco.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Origin", "https://candidate.adecco.it");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "https://candidate.adecco.it/candidate/reporting/searchoffer.asp");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", session_cookies);
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
        Elements elements = doc.select("td > span.SimpleText");
        String jobCountString = elements.first().text();
        int jobCount = 0;
        try {
            jobCount = Integer.parseInt(jobCountString.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
        }
        System.out.println("Lavori trovati per la provincia " + codice_provincia + ": " + jobCount);
        int next_start = 10;
        if (jobCount > 0) {
            //pagina iniziale
            scrapePage(doc);
            //pagine successive
            while (next_start < jobCount) {
                Document otherPageDoc = loadOtherPage(codice_provincia, next_start, jobCount);
                scrapePage(otherPageDoc);
                next_start += 10;
            }
        }
    }

    private Document loadOtherPage(int codice_provincia, int next_start, int count) throws Exception {
        String rawData = "cmbProvince1=" + codice_provincia + "&PreviousSearch=1&NextStart=" + next_start + "&Count=" + count + "&Caller=searchoffer.asp&lCurrentTop_Records=1000&lSearchNumber=0";
        String url = "https://candidate.adecco.it/candidate/reporting/searchoffer.asp";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "candidate.adecco.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Origin", "https://candidate.adecco.it");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "https://candidate.adecco.it/candidate/reporting/searchoffer.asp");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", session_cookies);
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
        return Jsoup.parse(response.toString());
    }

    private void scrapePage(Document doc) throws Exception {
        Element table = doc.select("tbody").get(3);
        Elements rows = table.select("tr");
        for (Element row : rows) {
            String rowString = row.toString();
            String rawData = rowString.substring(rowString.indexOf("(") + 1, rowString.indexOf(")"));
            String data[] = rawData.split(",");
            for (int i = 0; i < data.length; i++) {
                data[i] = data[i].replaceAll("'", "");
            }
            Elements tds = row.select("td");
            String place = tds.get(2).text();
            String title = tds.get(3).text();
            String publishedAtString = tds.get(4).text();
            Date publishedAtDate;
            try {
                java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(publishedAtString);
                publishedAtDate = new Date(utilDate.getTime());
            } catch (Exception e) {
                publishedAtDate = null;
            }
            String jobId = data[0] + "-" + data[1];
            if (DbUtils.jobExists(jobId, sourceId, region)) {
                jobSkipped++;
                System.out.println("annuncio saltato perché già presente");
            } else {
                String content = getContent(data[0], data[1], data[2]);
                JobPost job = new JobPost(sourceId, jobId, publishedAtString, publishedAtDate, null, title, place, content, region, null);
                DbUtils.saveJobPost(job);
                this.jobAdded++;
            }
        }
    }

    private String getContent(String data1, String data2, String status) {
        try {
            String rawData = "sBranchId=" + data1 + "&lOfferNumber=" + data2 + "&bVisualizar=false&sStatus=" + status + "&lSearchNumber=0";
            String url = "https://candidate.adecco.it/candidate/reporting/searchofferdetail.asp";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add request header
            con.setRequestMethod("POST");
            con.setRequestProperty("Host", "candidate.adecco.it");
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestProperty("Cache-Control", "max-age=0");
            con.setRequestProperty("Origin", "https://candidate.adecco.it");
            con.setRequestProperty("Upgrade-Insecure-Requests", "1");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            con.setRequestProperty("Referer", "https://candidate.adecco.it/candidate/reporting/searchoffer.asp");
            //con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
            con.setRequestProperty("Cookie", session_cookies);
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
            return doc.getElementById("TableOffer").text();
        } catch (Exception e) {
            return null;
        }
    }
}
