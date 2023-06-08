package uk.gov.ch.developer.docs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Configuration
public class WebSecurity {
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.securityMatcher("/**").addFilterBefore(new SessionHandler(),
                BasicAuthenticationFilter.class);

        return http.build();
    }
}