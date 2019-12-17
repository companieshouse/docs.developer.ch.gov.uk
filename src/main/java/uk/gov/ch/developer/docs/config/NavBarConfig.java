package uk.gov.ch.developer.docs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.models.NavBarItem;
import uk.gov.ch.developer.docs.models.NavBarModel;
import uk.gov.ch.developer.docs.models.NavItemList;

@Configuration
public class NavBarConfig {

    @Value("${gettingStarted.url}")
    private String gettingStartedURL;
    @Value("${home.url}")
    private String homeURL;
    @Value("${createAccount.url}")
    private String createAccountURL;
    @Value("${authentication.url}")
    private String authenticationUrl;
    @Value("${developerGuidelines.url}")
    private String devGuideURL;

    @Bean
    public NavBarModel getNavBarModel() {

        NavBarModel model = new NavBarModel();
        NavItemList manageApplications = model.addHeading("Manage Applications");

        manageApplications.add("View all applications", "example.com");
        manageApplications.add("Add an application", "example.com");

        NavItemList documentation = model.addHeading("Documentation");
        documentation.add("Companies House Api", homeURL);
        documentation.add("Overview", homeURL);
        NavBarItem gettingStarted = documentation.add("Getting Started", gettingStartedURL);
        gettingStarted.add("Create Account", createAccountURL);
        gettingStarted.add("Authentication", authenticationUrl);
        gettingStarted.add("Developer Guidelines", devGuideURL);
        documentation.add("API Documentation", homeURL);

        NavItemList manageAccounts = model.addHeading("Manage account");
        manageAccounts.add("Manage Profile", homeURL);
        manageAccounts.add("Change Password", homeURL);

        NavItemList help = model.addHeading("Help");
        help.add("Developer Hub Forum", homeURL);

        return model;
    }
}
