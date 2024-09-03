package uk.gov.ch.developer.docs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.companieshouse.csrf.config.ChsCsrfMitigationHttpSecurityBuilder;
import uk.gov.companieshouse.session.handler.SessionHandler;

@EnableWebSecurity
public class WebSecurity {

    private WebSecurity() {}

    @Configuration
    @Order(1)
    public static class RootSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
            return new ChsCsrfMitigationHttpSecurityBuilder(
                http.addFilterBefore(
                    new SessionHandler(), BasicAuthenticationFilter.class
                )
            )
            .withApiCsrfMitigations()
            .build()
            .build();
        }

    }

}