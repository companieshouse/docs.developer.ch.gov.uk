package uk.gov.ch.developer.docs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Configuration
public class WebSecurity {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                                .requestMatcher(AnyRequestMatcher.INSTANCE)
                        ))

                .addFilterBefore(new SessionHandler(), BasicAuthenticationFilter.class);
        return http.build();
    }
}