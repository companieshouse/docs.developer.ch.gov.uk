package uk.gov.ch.developer.docs.config;

import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.models.nav.DisplayRestrictions;
import uk.gov.ch.developer.docs.models.nav.NavBarModelBuilder;
import uk.gov.ch.developer.docs.models.nav.NavItemList;

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
    public NavBarModelBuilder getNavBarModelBuilder() {

        NavBarModelBuilder model = new NavBarModelBuilder();
        NavItemList manageApplications = model
                .addHeading("Manage Applications", EnumSet.of(DisplayRestrictions.USER_REQUIRED));

        manageApplications.add("View all applications", "example.com");
        manageApplications.add("Add an application", "example.com");

        NavItemList documentation = model
                .addHeading("General Documentation", DisplayRestrictions.NONE());
        documentation.add("Get Started", gettingStartedURL);
        documentation.add("Companies House REST API overview", homeURL);
        documentation.add("How to add an API key", homeURL);
        documentation.add("Developer Guidelines", devGuideURL);

        NavItemList manageAccounts = model
                .addHeading("Manage account", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
        manageAccounts.add("Manage Profile", homeURL);
        manageAccounts.add("Change Password", homeURL);

        NavItemList help = model.addHeading("Help", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
        help.add("Developer Hub Forum", homeURL);

        return model;
    }
}
