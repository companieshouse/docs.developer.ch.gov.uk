package uk.gov.ch.developer.docs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.IdentityProvider;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class OauthBean {

    @Autowired
    EnvironmentReader environmentReader;

    @Bean
    public IOauth oauth() {
        return new Oauth2(identityProvider());
    }

    @Bean
    IIdentityProvider identityProvider() {
        return new IdentityProvider(environmentReader);
    }
}
