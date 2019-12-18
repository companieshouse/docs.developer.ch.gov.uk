package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

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
