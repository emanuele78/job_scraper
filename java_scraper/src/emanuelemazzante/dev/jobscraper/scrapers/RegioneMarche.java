package emanuelemazzante.dev.jobscraper.scrapers;

import emanuelemazzante.dev.jobscraper.util.Pair;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import emanuelemazzante.dev.jobscraper.model.ScraperBase;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 *
 * @author emanuelemazzante
 */
public class RegioneMarche extends ScraperBase {

    private RegioneMarche(int sourceId, String region) {
        super(sourceId, region);
    }

    public static void startScrape(int sourceId, String region) {
        RegioneMarche regioneMarche = new RegioneMarche(sourceId, region);
        regioneMarche.start();
    }

    public void start() {
        ScraperReport report;
        try {
            System.out.println("\nINIZIO SCRAPER " + sourceId + "| REGIONE: " + region + "\n");
            //#1 fake landing
            test_landing_page_1();
            //#2 cookies
            ArrayList<String> cookies = test_grab_asp_cookie_2();
            //#3 first page
            Document firstPage = test_first_page_doc_3(cookies.get(0));
            //hidden dada from first page
            ArrayList<Pair> postData = test_get_post_data(firstPage);
            //lists of pages
            ArrayList<String> targetPages = test_get_target_pages(firstPage);
            //scraping jobs on first page
            test_scrape_jobs_in_current_page(firstPage, 1);
            for (int i = 1; i < targetPages.size(); i++) {
                //starting from index 1 = page 2
                //#4 loading following pages
                Document pageDoc = test_load_page_4(postData, targetPages.get(i), cookies.get(0));
                test_scrape_jobs_in_current_page(pageDoc, i + 1);
            }
            //done
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped);
        } catch (Exception e) {
            report = new ScraperReport(region, sourceId, jobAdded, jobSkipped, true);
        }
        DbUtils.addReport(report);
    }

    private ArrayList<String> test_landing_page_1() throws Exception {
        String url = "http://www.regione.marche.it/Entra-in-Regione/Centri-Impiego/Offerte-di-lavoro";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "www.regione.marche.it");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        //System.out.println(responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        Map<String, List<String>> headerFields = con.getHeaderFields();
        ArrayList<String> cookiesList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null) {
                if (entry.getKey().toLowerCase().equals("set-cookie")) {
                    List<String> list = entry.getValue();
                    for (String s : list) {
                        String[] splitted = s.split(";");
                        cookiesList.add(splitted[0]);
                    }
                }
            }
        }
        in.close();
        return cookiesList;
    }

    private ArrayList<String> test_grab_asp_cookie_2() throws Exception {
        String url = "http://84.38.50.174/BRL_Consulta_Vacancy/FREE/FiltroVacancy.aspx";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "84.38.50.174");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "http://www.regione.marche.it/Entra-in-Regione/Centri-Impiego/Offerte-di-lavoro");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setConnectTimeout(60000);
        con.setInstanceFollowRedirects(false);
        con.connect();
        int responseCode = con.getResponseCode();
        //System.out.println(responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        Map<String, List<String>> headerFields = con.getHeaderFields();
        ArrayList<String> cookiesList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null) {
                if (entry.getKey().toLowerCase().equals("set-cookie")) {
                    List<String> list = entry.getValue();
                    for (String s : list) {
                        String[] splitted = s.split(";");
                        cookiesList.add(splitted[0]);
                    }
                }
            }
        }
        in.close();
        return cookiesList;
    }

    private Document test_first_page_doc_3(String cookie) throws Exception {
        String url = "http://84.38.50.174/BRL_Consulta_Vacancy/FREE/ListaVacancy.aspx";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "84.38.50.174");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "http://www.regione.marche.it/Entra-in-Regione/Centri-Impiego/Offerte-di-lavoro");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", cookie);
        con.setConnectTimeout(60000);
        con.connect();
        int responseCode = con.getResponseCode();
        //System.out.println(responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        Document doc = Jsoup.parse(response.toString());
        return doc;
    }

    private ArrayList<Pair> test_get_post_data(Document doc) throws Exception {
        ArrayList<Pair> postData = new ArrayList<>();
        postData.add(new Pair("__EVENTARGUMENT", doc.getElementById("__EVENTARGUMENT").attr("value")));
        postData.add(new Pair("__VIEWSTATE", doc.getElementById("__VIEWSTATE").attr("value")));
        postData.add(new Pair("__VIEWSTATEGENERATOR", doc.getElementById("__VIEWSTATEGENERATOR").attr("value")));
        postData.add(new Pair("__EVENTVALIDATION", doc.getElementById("__EVENTVALIDATION").attr("value")));
        postData.add(new Pair("__EVENTARGUMENT", doc.getElementById("__EVENTARGUMENT").attr("value")));
        postData.add(new Pair("__EVENTARGUMENT", doc.getElementById("__EVENTARGUMENT").attr("value")));
        postData.add(new Pair("__EVENTARGUMENT", doc.getElementById("__EVENTARGUMENT").attr("value")));
        return postData;
    }

    private ArrayList<String> test_get_target_pages(Document doc) throws Exception {
        Element content = doc.getElementById("divcontent");
        Elements lists = content.select("ul");
        //first element: first page jobs
        //second element: next pages targets
        Elements linkPagine = lists.last().select("li");
        ArrayList<String> targetPages = new ArrayList<>();
        for (Element link : linkPagine) {
            String value = link.select("a").first().attr("href");
            int firstIndex = value.indexOf("'");
            int lastIndex = value.indexOf("'", firstIndex + 1);
            targetPages.add(value.substring(firstIndex + 1, lastIndex));
        }
        return targetPages;
    }

    private Document test_load_page_4(ArrayList<Pair> postData, String targetPage, String aspkey) throws Exception {
        String urlParams = "__EVENTTARGET=" + URLEncoder.encode(targetPage, StandardCharsets.UTF_8.toString());
        for (Pair data : postData) {
            if (data.getKey().equals("__VIEWSTATE") || data.getKey().equals("__VIEWSTATEGENERATOR") || data.getKey().equals("__EVENTVALIDATION")) {
                urlParams += "&" + data.getKey() + "=" + URLEncoder.encode(data.getValue(), StandardCharsets.UTF_8.toString());
            }
        }
        String url = "http://84.38.50.174/BRL_Consulta_Vacancy/FREE/ListaVacancy.aspx";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "84.38.50.174");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Origin", "http://84.38.50.174");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "http://84.38.50.174/BRL_Consulta_Vacancy/FREE/ListaVacancy.aspx");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        con.setRequestProperty("Cookie", aspkey);
        con.setConnectTimeout(60000);
        // Send post request
        con.setDoOutput(true);
        OutputStreamWriter w = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
        w.write(urlParams);
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

    private void test_scrape_jobs_in_current_page(Document doc, int pageIndex) throws Exception {
        Element content = doc.getElementById("divcontent");
        Elements lists = content.select("ul");
        //first element: first page jobs
        //second element: next pages targets
        Elements annunci = lists.first().select("li");
        System.out.println("*** Inizio scraping pagina " + pageIndex);
        int cont = 1;
        for (Element e : annunci) {
            System.out.println("scraping annunci n. " + cont++);
            String percorso = e.select("a[href]").last().attr("href");
            loadDetailsPage(percorso);
        }
    }

    private void loadDetailsPage(String percorso) throws Exception {
        String url = "http://84.38.50.174/BRL_Consulta_Vacancy/FREE/" + percorso;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Host", "84.38.50.174");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Upgrade-Insecure-Requests", "1");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
        con.setRequestProperty("Accept", "﻿text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        con.setRequestProperty("Referer", "﻿http://84.38.50.174/BRL_Consulta_Vacancy/FREE/ListaVacancy.aspx");
        //con.setRequestProperty("Accept-Encoding", "gzip, deflate");
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
        parseJob(response.toString(), url);
    }

    private void parseJob(String pageContent, String link) throws Exception {
        Document doc = Jsoup.parse(pageContent);
        Elements tableRows = doc.select("tbody").first().select("td");
        ArrayList<String> raw = new ArrayList<>();
        for (Element row : tableRows) {
            String currentRow = row.toString().replace("<td>", "").replace("</td>", "").replace("<td data-name=\"CIOF\">", "").trim() + "\n";
            raw.add(currentRow);
        }
        JobPost jobPost = createJobPostFromRaw(raw, link);
        boolean jobExists = DbUtils.jobExists(jobPost.getJobId(), sourceId, region);
        if (jobExists) {
            jobSkipped++;
        } else {
            DbUtils.saveJobPost(jobPost);
            jobAdded++;
        }
    }

    private JobPost createJobPostFromRaw(ArrayList<String> raw, String link) throws Exception {
        String jobId = null;
        String title = null;
        String place = null;
        String description = "";
        boolean placeAdded = false;
        boolean titleAdded = false;
        boolean jobIdAdded = false;
        for (int i = 0; i < raw.size(); i++) {
            String currentRaw = raw.get(i).toLowerCase();
            if (currentRaw.contains("sede lavoro") && !placeAdded) {
                place = raw.get(i + 1).trim();
                placeAdded = true;
            } else if (currentRaw.contains("qualifica") && !titleAdded) {
                title = raw.get(i + 1).trim();
                titleAdded = true;
            } else if (currentRaw.contains("codice offerta") && !jobIdAdded) {
                jobId = raw.get(i + 1).trim();
                jobIdAdded = true;
            }
            description += currentRaw.trim() + "\n";
        }
        return new JobPost(sourceId, jobId, null, null, null, title, place, description, region, link);
    }
}
