package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.companieshouse.logging.Logger;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class SignOutController {
    private final Logger logger;

    private final String redirectUri;

    public SignOutController(Logger logger, @Value("${signout.redirect.url}") String redirectUri) {
        this.logger = logger;
        this.redirectUri = redirectUri;
    }

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse) throws IOException {

        logger.debug("Processing sign out. Redirecting to " + redirectUri);

        httpServletResponse.sendRedirect(redirectUri);
    }
}