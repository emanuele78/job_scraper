package emanuelemazzante.dev.jobscraper.model;

/**
 *
 * @author emanuelemazzante
 */
public class ScraperReport {

    private final String region;
    private final int sourceId;
    private final int jobs_added;
    private final int jobs_skipped;
    private final boolean error;

    public ScraperReport(String region, int sourceId, int jobs_added, int jobs_skipped) {
        this.region = region;
        this.sourceId = sourceId;
        this.jobs_added = jobs_added;
        this.jobs_skipped = jobs_skipped;
        this.error = false;
    }

    public ScraperReport(String region, int sourceId, int jobs_added, int jobs_skipped, boolean error) {
        this.region = region;
        this.sourceId = sourceId;
        this.jobs_added = jobs_added;
        this.jobs_skipped = jobs_skipped;
        this.error = error;
    }

    public String getRegion() {
        return region;
    }

    public int getSourceId() {
        return sourceId;
    }

    public int getJobs_added() {
        return jobs_added;
    }

    public int getJobs_skipped() {
        return jobs_skipped;
    }

    public boolean isError() {
        return error;
    }

}
