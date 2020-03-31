package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
public class SignOutController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    private final String signOutUrl;

    public SignOutController(final EnvironmentReader reader) {
        this.signOutUrl = reader.getMandatoryString("CHS_SIGNOUT_URL");
    }

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse) throws IOException {

        LOGGER.debug("Processing redirect to sign out");

        httpServletResponse.sendRedirect(signOutUrl);
    }

}
