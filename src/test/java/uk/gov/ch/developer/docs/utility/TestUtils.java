package uk.gov.ch.developer.docs.utility;

import java.util.Properties;

public class TestUtils {

    /**
     * This injects required properties for mock mvc environments that are created off of the
     * application context. It needs to be run before the Spring environment is created so needs to
     * be run in a static constructor.
     */
    public static void setUpEnviromentProperties() {
        Properties p = System.getProperties();
        p.setProperty("cdn.url", "//");
        p.setProperty("chs.url", "//");
        p.setProperty("piwik.url", "//");
        p.setProperty("piwik.siteId", "//");

        p.setProperty("home.url", "/");
        p.setProperty("overview.url", "/overview");
        p.setProperty("developerGuidelines.url", "${home.url}/developer-guidelines");
        p.setProperty("createAccount.url", "${home.url}/create-account");
        p.setProperty("getStarted.url", "${home.url}/get-started");
        System.setProperties(p);
    }
}
