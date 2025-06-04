package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.ch.developer.docs.DocsWebApplication.APPLICATION_NAME_SPACE;

@Configuration
public class LoggingConfig {
    @Bean
    Logger getLogger() {
        return LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    }
}
