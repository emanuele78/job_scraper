/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emanuelemazzante.dev.jobscraper.util;

import emanuelemazzante.dev.jobscraper.Main;
import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author emanuelemazzante
 */
public class Utils {
    
    public static String getExecutablePath() {
        try {
            String path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            final String PRODUCTION_CLASS = "/build/classes";
            if (path.contains(PRODUCTION_CLASS)) {
                return path.replace(PRODUCTION_CLASS, "") + "/dist";
            } else {
                return path.replace("/JobScraper.jar", "");
            }
        } catch (URISyntaxException ex) {
            System.out.println("Unable to get path");
            System.exit(0);
            return null;
        }
    }
}
