package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig class implements the WebMvcConfigurer interface to customize the configuration of Spring MVC.
 * It is annotated with @Configuration and @EnableWebMvc to indicate that it provides configuration for the application.
 * <p>
 * Configuration to map resource locations to logical URLs.
 */

@Configuration
@EnableWebMvc
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

}
