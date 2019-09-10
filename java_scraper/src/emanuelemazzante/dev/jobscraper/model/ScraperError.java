package emanuelemazzante.dev.jobscraper.model;

/**
 *
 * @author emanuelemazzante
 */
public class ScraperError {
    
    private final String source;
    private final String region;
    private final String description;

    public ScraperError(String source, String region, String description) {
        this.source = source;
        this.region = region;
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public String getRegion() {
        return region;
    }

    public String getDescription() {
        return description;
    }
    
    
}
