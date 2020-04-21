package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.identity.IdentityProvider;
import uk.gov.ch.oauth.session.SessionUtils;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class OauthBean {

    @Bean
    public IOauth oauth(IIdentityProvider identityProvider, SessionUtils sessionUtils) {
        return new Oauth2(identityProvider, sessionUtils);
    }

    @Bean
    IIdentityProvider identityProvider(EnvironmentReader environmentReader) {
        return new IdentityProvider(environmentReader);
    }

    @Bean
    SessionUtils sessionUtils() {
        return new SessionUtils();
    }
}
