package uk.gov.ch.developer.docs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.ch.developer.docs.interceptor.LoggingInterceptor;

@Configuration
@ComponentScan("uk.gov.companieshouse.authentication.interceptor")
public class InterceptorConfig implements WebMvcConfigurer {

    private LoggingInterceptor loggingInterceptor;


    public InterceptorConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
    }

    /**
     * Setup the interceptors to run against endpoints when the endpoints are called
     * Interceptors are executed in the order they are added to the registry
     *
     * @param registry The spring interceptor registry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor).excludePathPatterns(
                "/favicon.ico",
                "/img/**",
                "/assets/images/**",
                "/css/**"
        );
    }
}
