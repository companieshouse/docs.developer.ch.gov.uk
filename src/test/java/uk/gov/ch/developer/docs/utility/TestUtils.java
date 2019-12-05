package uk.gov.ch.developer.docs.utility;

import java.util.Properties;

public class TestUtils {

    public static void setUpEnviromentProperties() {
        Properties p = System.getProperties();
        p.setProperty("cdn.url", "//");
        p.setProperty("chs.url", "//");
        p.setProperty("piwik.url", "//");
        p.setProperty("piwik.siteId", "//");
        p.setProperty("home.url", "/dev-hub");
        p.setProperty("howToCreateAPIKey.url", "${home.url}/create-api-key");
        System.setProperties(p);
    }
}
