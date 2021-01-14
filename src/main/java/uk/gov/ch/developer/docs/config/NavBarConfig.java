package uk.gov.ch.developer.docs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.models.nav.DisplayRestrictions;
import uk.gov.ch.developer.docs.models.nav.NavBarModelBuilder;
import uk.gov.ch.developer.docs.models.nav.NavItemList;

@Configuration
public class NavBarConfig {

    @Value("${getStarted.url}")
    private String getStartedURL;
    @Value("${home.url}")
    private String homeURL;
    @Value("${createAccount.url}")
    private String createAccountURL;
    @Value("${authentication.url}")
    private String authenticationUrl;
    @Value("${developerGuidelines.url}")
    private String devGuideURL;
    @Value("${overview.url}")
    private String overviewURL;
    @Value("${manageApplications.url}")
    private String manageApplicationsURL;
    @Value("${addApplication.url}")
    private String addApplicationURL;
    @Value("${howToCreateApplication.url}")
    private String howToCreateApplicationURL;
    @Value("${developerSpecs.url}")
    private String developerSpecsURL;

    /**
     * Constructs the entire model along with visibility restrictions.
     *
     * @return builder that constructs visible navigation bar for a given session.
     */
    @Bean
    public NavBarModelBuilder getNavBarModelBuilder() {

        NavBarModelBuilder model = new NavBarModelBuilder();
        NavItemList manageApplications = model
                .addHeading("Manage applications", DisplayRestrictions.USER_REQUIRED);
        manageApplications.add("View all applications", manageApplicationsURL);
        manageApplications.add("Create an application", addApplicationURL);

        NavItemList documentation = model
                .addHeading("General documentation", DisplayRestrictions.none());
        documentation.add("Get started", getStartedURL);
        documentation.add("Companies House REST API overview", overviewURL);
        documentation.add("How to create an application", howToCreateApplicationURL);
        documentation.add("API authentication", authenticationUrl);
        documentation.add("Developer guidelines", devGuideURL);
        
        NavItemList specs = model.addHeading("API specifications", DisplayRestrictions.none());
        specs.add("API specifications list", developerSpecsURL);

        return model;
    }
}
