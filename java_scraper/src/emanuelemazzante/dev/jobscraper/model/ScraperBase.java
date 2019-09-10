package emanuelemazzante.dev.jobscraper.model;

/**
 *
 * @author emanuelemazzante
 */
public class ScraperBase {

    protected int jobAdded;
    protected int jobSkipped;
    protected final String region;
    protected final int sourceId;

    public ScraperBase(int sourceId, String region) {
        this.sourceId = sourceId;
        this.region = region;
    }

}
