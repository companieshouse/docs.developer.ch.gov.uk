package uk.gov.ch.developer.docs.interceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final Logger logger;
    private RequestLogger requestLogger = new RequestLogger() {
        // Implement Defaults
    };

    public LoggingInterceptor() {
        this(LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE));
    }

    public LoggingInterceptor(final Logger logger) {
        this.logger = logger;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        requestLogger.logStartRequestProcessing(request, logger);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) {
        requestLogger.logEndRequestProcessing(request, response, logger);
    }
}
