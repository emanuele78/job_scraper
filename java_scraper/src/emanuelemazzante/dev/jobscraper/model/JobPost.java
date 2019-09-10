package emanuelemazzante.dev.jobscraper.model;

import java.sql.Date;

/**
 *
 * @author emanuelemazzante
 */
public class JobPost {

    private int sourceId;
    private String jobId;
    private String publishedAtString;
    private Date publishedAtDate;
    private String companyName;
    private String title;
    private String place;
    private String description;
    private String region;
    private String link;

    public JobPost(int sourceId, String jobId, String publishedAtString, Date publishedAtDate, String companyName, String title, String place, String description, String region, String link) {
        this.sourceId = sourceId;
        this.jobId = jobId;
        this.publishedAtString = publishedAtString;
        this.publishedAtDate = publishedAtDate;
        this.companyName = companyName;
        this.title = title;
        this.place = place;
        this.description = description;
        this.region = region;
        this.link = link;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPublishedAtString() {
        return publishedAtString;
    }

    public void setPublishedAtString(String publishedAtString) {
        this.publishedAtString = publishedAtString;
    }

    public Date getPublishedAtDate() {
        return publishedAtDate;
    }

    public void setPublishedAtDate(Date publishedAtDate) {
        this.publishedAtDate = publishedAtDate;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
