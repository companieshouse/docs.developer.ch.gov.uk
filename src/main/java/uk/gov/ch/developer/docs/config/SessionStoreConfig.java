package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.session.store.Store;
import uk.gov.companieshouse.session.store.StoreImpl;

@Configuration
public class SessionStoreConfig {
    
    @Bean
    public Store store() {
        return new StoreImpl();
    }

}
