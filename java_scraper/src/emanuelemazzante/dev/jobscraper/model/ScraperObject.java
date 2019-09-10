package emanuelemazzante.dev.jobscraper.model;

/**
 *
 * @author emanuelemazzante
 */
public class ScraperObject {

    private final int id;
    private final boolean enabled;
    private final String name;

    public ScraperObject(int id, boolean enabled, String name) {
        this.id = id;
        this.enabled = enabled;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

}
