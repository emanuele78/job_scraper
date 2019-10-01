package emanuelemazzante.dev.jobscraper;

import emanuelemazzante.dev.jobscraper.scrapers.RegioneMarche;
import emanuelemazzante.dev.jobscraper.scrapers.Adecco;
import emanuelemazzante.dev.jobscraper.scrapers.GiGroup;
import emanuelemazzante.dev.jobscraper.scrapers.Indeed;
import java.util.ArrayList;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import emanuelemazzante.dev.jobscraper.scrapers.ManPower;
import emanuelemazzante.dev.jobscraper.scrapers.Monster;
import emanuelemazzante.dev.jobscraper.scrapers.Randstad;
import emanuelemazzante.dev.jobscraper.scrapers.Subito;
import emanuelemazzante.dev.jobscraper.util.DbUtils;
import emanuelemazzante.dev.jobscraper.model.ScraperObject;
import emanuelemazzante.dev.jobscraper.scrapers.Linkedin;
import emanuelemazzante.dev.jobscraper.scrapers.PortaleLavoro;

public class Main {

    private static final boolean USE_FIDDLER = false;
    private static final int SCRAPE_ALL = -1;
    private static final int SCRAPE_ONLY_THIS_SOURCE = 1;
    

    private static void setFiddler() {
        setTrustAllCerts();
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8888");
        System.setProperty("https.proxyPort", "8888");
    }

    public static void main(String[] args) throws Exception {        
        //setting region
        final String REGION = "marche";
        //check for execute only
        int executeOnly = SCRAPE_ONLY_THIS_SOURCE;
        if(args.length !=0){
            executeOnly = Integer.parseInt(args[0]);
        }
        //check for fiddler logging
        if (USE_FIDDLER) {
            setFiddler();
        }
        //start
        if (DbUtils.startIfNotRunning()) {
            startScraping(REGION, executeOnly);
            boolean isRunning = false;
            DbUtils.setStatus(isRunning);
        }
    }

    private static void startScraping(String region, int executeOnly) {
        ArrayList<ScraperObject> scrapers = DbUtils.readScrapersList();
        for (ScraperObject scraper : scrapers) {
            if (scraper.isEnabled() && (executeOnly == -1 || executeOnly == scraper.getId())) {
                int scraperId = scraper.getId();
                switch (scraper.getName()) {
                    case "indeed":
                        Indeed.startScrape(scraperId, region);
                        break;
                    case "ci":
                        RegioneMarche.startScrape(scraperId, region);
                        break;
                    case "monster":
                        Monster.startScrape(scraperId, region);
                        break;
                    case "gigroup":
                        GiGroup.startScrape(scraperId, region);
                        break;
                    case "adecco":
                        Adecco.startScrape(scraperId, region);
                        break;
                    case "randstad":
                        Randstad.startScrape(scraperId, region);
                        break;
                    case "manpower":
                        ManPower.startScrape(scraperId, region);
                        break;
                    case "subito":
                        Subito.startScrape(scraperId, region);
                        break;
                    case "linkedin":
                        Linkedin.startScrape(scraperId, region);
                        break;
                    case "portalelavoro":
                        PortaleLavoro.startScrape(scraperId, region);
                        break;
                    default:
                        System.out.println("Scraper non supportato");
                }
            }
        }
    }

    private static void setTrustAllCerts() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
            };
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(
                        new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
