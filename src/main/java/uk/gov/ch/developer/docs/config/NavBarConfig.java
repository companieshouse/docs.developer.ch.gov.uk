package uk.gov.ch.developer.docs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.models.nav.NavBarModel;
import uk.gov.ch.developer.docs.models.nav.NavItemList;
import uk.gov.ch.developer.docs.models.nav.UserRequired;

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
        NavItemList manageApplications = model
                .addHeading("Manage Applications", UserRequired.userRequired);

        manageApplications.add("View all applications", "example.com");
        manageApplications.add("Add an application", "example.com");

        NavItemList documentation = model
                .addHeading("General Documentation", UserRequired.userNotRequired);
        documentation.add("Getting Started", gettingStartedURL);
        documentation.add("Companies House REST API overview", homeURL);
        documentation.add("Overview", homeURL);
        documentation.add("Developer Guidelines", devGuideURL);

        NavItemList manageAccounts = model.addHeading("Manage account", UserRequired.userRequired);
        manageAccounts.add("Manage Profile", homeURL);
        manageAccounts.add("Change Password", homeURL);

        NavItemList help = model.addHeading("Help", UserRequired.userRequired);
        help.add("Developer Hub Forum", homeURL);

        return model;
    }
}
