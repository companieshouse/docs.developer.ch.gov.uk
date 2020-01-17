package uk.gov.ch.developer.docs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.ch.developer.docs.interceptor.LoggingInterceptor;
import uk.gov.ch.developer.docs.interceptor.UserDetailsInterceptor;

@SpringBootApplication
public class DocsWebApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "docs.developer.ch.gov.uk";


    private LoggingInterceptor loggingInterceptor;
    private UserDetailsInterceptor userDetailsInterceptor;

    @Autowired
    public DocsWebApplication(LoggingInterceptor loggingInterceptor,
            UserDetailsInterceptor userDetailsInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
        this.userDetailsInterceptor = userDetailsInterceptor;
    }

    public static void main(String[] args) {
        SpringApplication.run(DocsWebApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        /**
         * The below should be uncommented when the login journey is successfully implemented
         */
//        registry.addInterceptor(userDetailsInterceptor);
    }
}
