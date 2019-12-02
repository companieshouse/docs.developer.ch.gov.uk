package uk.gov.ch.developer.docs.interceptor;


import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    private RequestLogger requestLogger = new RequestLogger() {
        // Implement Defaults
    };

    private Logger LOGGER = LoggerFactory
            .getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        requestLogger.logStartRequestProcessing(request, LOGGER);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) {

        requestLogger.logEndRequestProcessing(request, response, LOGGER);
    }
}
