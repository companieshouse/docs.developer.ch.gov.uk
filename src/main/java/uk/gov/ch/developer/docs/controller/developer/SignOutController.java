package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SignOutController {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    private final String redirectUri;

    public SignOutController(@Value("${signout.redirect.url}") String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse) throws IOException {

        LOGGER.debug("Processing sign out. Redirecting to " + redirectUri);

        httpServletResponse.sendRedirect(redirectUri);
    }
}