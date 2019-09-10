package emanuelemazzante.dev.jobscraper.util;

import emanuelemazzante.dev.jobscraper.model.ScraperReport;
import emanuelemazzante.dev.jobscraper.model.ScraperObject;
import emanuelemazzante.dev.jobscraper.model.JobPost;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;

public class DbUtils {

    public static boolean jobExists(String jobId, int sourceId, String region) throws Exception {
        boolean exists = false;
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "select id from job_posts where job_id=? and source_id=? and region=?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setString(1, jobId);
            ps.setInt(2, sourceId);
            ps.setString(3, region);
            ResultSet rs = ps.executeQuery();
            exists = rs.next();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return exists;
    }

    public static boolean startIfNotRunning() {
        boolean isRunning = readStatus();
        if (isRunning) {
            return false;
        }
        setStatus(true);
        return true;
    }

    public static boolean readStatus() {
        boolean isRunning = true;
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "select is_running from scraper_status where id=1";
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                isRunning = rs.getBoolean("is_running");
            } else {
                throw new Exception("record not found");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return isRunning;
    }

    public static void setStatus(boolean status) {
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "UPDATE scraper_status SET is_running=? WHERE id=?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setBoolean(1, status);
            ps.setInt(2, 1);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static ArrayList<ScraperObject> readScrapersList() {
        ArrayList<ScraperObject> scrapers = new ArrayList<>();
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "select id, name, enabled from scrapers";
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                scrapers.add(new ScraperObject(rs.getInt("id"), rs.getBoolean("enabled"), rs.getString("name")));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return scrapers;
    }

    public static void saveJobPost(JobPost jobPost) throws Exception {
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "INSERT INTO job_posts (source_id, job_id, published_at_string, published_at_date, company_name, title, place, description, region, post_link) values(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, jobPost.getSourceId());
            ps.setString(2, jobPost.getJobId());
            ps.setString(3, jobPost.getPublishedAtString());
            ps.setDate(4, jobPost.getPublishedAtDate());
            ps.setString(5, jobPost.getCompanyName());
            ps.setString(6, jobPost.getTitle());
            ps.setString(7, jobPost.getPlace());
            ps.setString(8, jobPost.getDescription());
            ps.setString(9, jobPost.getRegion());
            ps.setString(10, jobPost.getLink());
            ps.executeUpdate();
            System.out.println("annuncio inserito");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public static void addReport(ScraperReport scraperReport) {
        int reportId = reportExists(scraperReport.getSourceId(), scraperReport.getRegion());
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query;
            PreparedStatement ps;
            if (reportId != -1) {
                query = "DELETE FROM reports WHERE id=?";
                ps = cn.prepareStatement(query);
                ps.setInt(1, reportId);
                ps.executeUpdate();
            }
            query = "INSERT INTO reports (source_id, region, jobs_added, jobs_skipped, error) values(?,?,?,?,?)";
            ps = cn.prepareStatement(query);
            ps.setInt(1, scraperReport.getSourceId());
            ps.setString(2, scraperReport.getRegion());
            ps.setInt(3, scraperReport.getJobs_added());
            ps.setInt(4, scraperReport.getJobs_skipped());
            ps.setBoolean(5, scraperReport.isError());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private static int reportExists(int sourceId, String region) {
        int id = -1;
        try (Connection cn = emanuelemazzante.dev.jobscraper.util.Connection.getConnection()) {
            String query = "select id from reports where source_id=? and region=?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, sourceId);
            ps.setString(2, region);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return id;
    }

}
