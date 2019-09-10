package emanuelemazzante.dev.jobscraper.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.DriverManager;
import java.util.Properties;

/**
 *
 * @author emanuelemazzante
 */
public class Connection {

    private static String dbName;
    private static String user;
    private static String password;
    private static String host;
    private static String port;

    static {
        File file = new File(System.getProperty("user.dir") + "/settings/db_connection.txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                if (!st.startsWith("#")) {
                    String settings[] = st.split("=");
                    switch (settings[0]) {
                        case "DBNAME":
                            Connection.dbName = settings[1].trim();
                            break;
                        case "USER":
                            Connection.user = settings[1].trim();
                            break;
                        case "PASSWORD":
                            Connection.password = settings[1].trim();
                            break;
                        case "HOST":
                            Connection.host = settings[1].trim();
                            break;
                        case "PORT":
                            Connection.port = settings[1].trim();
                            break;
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static java.sql.Connection getConnection() throws Exception {
        String urlMYSQL = "jdbc:mysql://" + host + ":" + port + "/";
        String driverMYSQL = "com.mysql.jdbc.Driver";
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        Class.forName(driverMYSQL).newInstance();
        return DriverManager.getConnection(urlMYSQL + dbName, properties);
    }
}
