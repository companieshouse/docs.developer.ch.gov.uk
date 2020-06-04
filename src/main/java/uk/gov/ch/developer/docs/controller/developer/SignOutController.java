package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Controller
public class SignOutController {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private IIdentityProvider identityProviders;
    @Autowired
    private Oauth2 oauth2;

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse,
                          final HttpServletRequest httpServletRequest) throws IOException {

        LOGGER.debug("Processing sign out");

        final Session chSession = (Session) httpServletRequest
                .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);
        oauth2.invalidateSession(chSession);
        httpServletResponse.sendRedirect(identityProviders.getRedirectUriPage());
    }

}
