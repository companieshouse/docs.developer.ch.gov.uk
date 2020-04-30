package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.identity.IdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class OauthBean {

    @Bean
    public IOauth oauth(IIdentityProvider identityProvider, SessionFactory sessionFactory) {
        return new Oauth2(identityProvider, sessionFactory);
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
