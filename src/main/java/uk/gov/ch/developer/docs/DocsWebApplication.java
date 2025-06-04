package uk.gov.ch.developer.docs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DocsWebApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "docs.developer.ch.gov.uk";

    public static void main(String[] args) {
        SpringApplication.run(DocsWebApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }
}
