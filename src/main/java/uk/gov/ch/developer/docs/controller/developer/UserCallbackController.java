package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private IIdentityProvider identityProvider;

    @Autowired
    private IOauth oauth;

    @GetMapping(params = {"state", "code"})
    public void getCallback(@RequestParam("state") String state, @RequestParam("code") String code,
            final HttpServletResponse httpServletResponse) {
        try {
            final boolean valid = oauth.validate(state, code, httpServletResponse);
            if (valid) {
                httpServletResponse.sendRedirect(identityProvider.getRedirectUriPage());
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (final Exception e) {
            LOGGER.error(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(params = {"state", "error"})
    public void accessRefused(@RequestParam("error") String error,
            final HttpServletResponse httpServletResponse) throws IOException {
        LOGGER.error("Was not granted access to user information");
        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, error);
    }
}
