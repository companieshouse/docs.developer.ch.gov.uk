package uk.gov.ch.developer.docs.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import uk.gov.companieshouse.session.handler.SessionHandler;

@EnableWebSecurity
public class WebSecurity {

    @Configuration
    @Order(1)
    public static class RootSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/").addFilterBefore(new SessionHandler(),
                    BasicAuthenticationFilter.class);
        }
    }
    @Configuration
    @Order(2)
    public static class SignInSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/signin").addFilterBefore(new SessionHandler(),
                    BasicAuthenticationFilter.class);
        }
    }

    @Configuration
    @Order(3)
    public static class CallbackSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/oauth2/user/**").addFilterBefore(new SessionHandler(),
                    BasicAuthenticationFilter.class);
        }
    }

}
