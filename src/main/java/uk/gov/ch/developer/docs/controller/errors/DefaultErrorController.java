package uk.gov.ch.developer.docs.controller.errors;

import java.util.HashMap;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.developer.docs.controller.AbstractPageController;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
@RequestMapping(DefaultErrorController.ERROR_MAPPING)

public class DefaultErrorController extends AbstractPageController implements ErrorController {


    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);
    private static final String TITLE = "Error in Request";
    public static final String ERROR_MAPPING = "/error";

    @Autowired
    HttpServletRequest request;
    @Value("${error.pageNotFound.path}")
    String notFoundPath;

    @Value("${error.internalService.path}")
    String serviceError;

    public DefaultErrorController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return handleError();
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    private String handleError() {
        // get error status
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        logError();

        if (status != null) {
            var statusCode = Integer.parseInt(status.toString());
            // display specific error page
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return getNotFoundPath();
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return getServiceErrorPath();
            }
        }
        // display generic error
        return "error";
    }

    void logError() {
        HashMap<String, Object> errorMap = new HashMap<>();
        errorMap.put("Message",
                getRequestAttributeAsString(RequestDispatcher.ERROR_MESSAGE));
        errorMap.put("Servlet Name",
                getRequestAttributeAsString(RequestDispatcher.ERROR_SERVLET_NAME));
        errorMap.put("Error Code",
                getRequestAttributeAsString(RequestDispatcher.ERROR_STATUS_CODE));

        LOGGER.error("", errorMap);
    }

    private String getRequestAttributeAsString(String attribute) {
        try {
            return request.getAttribute(attribute).toString();
        } catch (NullPointerException e) {
            LOGGER.debug(String.format("Could not find %s in request attributes.", attribute));
            return null;
        }
    }

    String getNotFoundPath() {
        return notFoundPath;
    }

    String getServiceErrorPath() {
        return serviceError;
    }
}