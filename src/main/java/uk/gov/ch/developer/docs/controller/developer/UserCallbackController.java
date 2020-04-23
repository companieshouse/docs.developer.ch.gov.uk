package uk.gov.ch.developer.docs.controller.developer;

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

    @GetMapping
    public void getCallback(@RequestParam("state") String state, @RequestParam("code") String code,
            final HttpServletResponse httpServletResponse) {
        LOGGER.trace("Code:" + code);
        LOGGER.trace("State:" + state);
        try {
            final boolean valid = oauth.isValid(state, code);
            if (valid) {
                httpServletResponse.sendRedirect(identityProvider.getRedirectUriPage());
            } else {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (final Exception e) {
            LOGGER.error(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
