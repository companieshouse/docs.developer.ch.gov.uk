package uk.gov.ch.developer.docs.utility;

import java.util.Properties;

public class TestUtils {
    /**
     * This injects required properties for mock mvc environments that are created off of the application context.
     * It needs to be run before the Spring environment is created so needs to be run in a static constructor.
     */
    public static void setUpEnviromentProperties() {
        Properties p = System.getProperties();
        p.setProperty("cdn.url", "//");
        p.setProperty("chs.url", "//");
        p.setProperty("piwik.url", "//");
        p.setProperty("piwik.siteId", "//");
        p.setProperty("home.url", "/dev-hub");
        p.setProperty("howToObtainAPIKey.url", "${home.url}/obtain-api-key");
        p.setProperty("createAccount.url", "${home.url}/create-account");
        System.setProperties(p);
    }
}
