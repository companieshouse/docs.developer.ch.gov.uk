package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.companieshouse.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;

import static uk.gov.ch.developer.docs.DocsWebApplication.APPLICATION_NAME_SPACE;

@Controller
public class SignOutController {

    @Value("${signout.redirect.url}")
    private String redirectUri;

    private static final Logger logger = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse) throws IOException {

        logger.debug("Processing sign out. Redirecting to " + redirectUri);

        httpServletResponse.sendRedirect(redirectUri);
    }
}