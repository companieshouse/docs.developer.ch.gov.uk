package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.ch.oauth.OAuthCoordinator;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.identity.IdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class OauthBean {

    @Bean
    public IOAuthCoordinator oAuthCoordinator() {
        return new OAuthCoordinator("docs.developer.ch.gov.uk");
    }

    @Bean
    IIdentityProvider identityProvider(EnvironmentReader environmentReader) {
        return new IdentityProvider(environmentReader);
    }

    @Bean
    SessionFactory sessionFactory() {
        return new SessionFactory();
    }
}
