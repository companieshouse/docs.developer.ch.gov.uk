package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.ch.oauth.OAuthCoordinator;

@Configuration
public class OauthBean {

    @Bean
    public IOAuthCoordinator oAuthCoordinator() {
        return new OAuthCoordinator(DocsWebApplication.APPLICATION_NAME_SPACE);
    }

}