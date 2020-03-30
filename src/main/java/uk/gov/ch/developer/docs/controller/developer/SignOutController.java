package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
@RequestMapping("${signout.url}")
public class SignOutController {
    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    private final String signOutUrl;

    public SignOutController(EnvironmentReader reader) {
        this.signOutUrl = reader.getMandatoryString("CHS_SIGNOUT_URL");
    }

    @GetMapping
    public void doSignOut(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        LOGGER.debug("Processing redirect to sign out");

        httpServletResponse.sendRedirect(signOutUrl);
    }

}
