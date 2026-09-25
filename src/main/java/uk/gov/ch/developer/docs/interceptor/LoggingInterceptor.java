package uk.gov.ch.developer.docs.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.RequestLogger;

import static uk.gov.ch.developer.docs.DocsWebApplication.APPLICATION_NAME_SPACE;

@Component
public class LoggingInterceptor implements HandlerInterceptor, RequestLogger {

    private final Logger logger = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        logStartRequestProcessing(request, logger);
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                           @Nullable ModelAndView modelAndView) {
        logEndRequestProcessing(request, response, logger);
    }
}
