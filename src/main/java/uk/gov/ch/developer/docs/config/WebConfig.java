package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
/**
 * configuration to map resource locations to logical urls
 */
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/img/**",
                "/assets/images/**",
                "/css/**"
        )
                .addResourceLocations(
                        "classpath:/static/img/",
                        "classpath:/static/img/",
                        "classpath:/static/css/"
                );
    }

    @Override
    @SuppressWarnings("deprecation") // Trailing slash support deprecated in Spring Boot 3.
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
