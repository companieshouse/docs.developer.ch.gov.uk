package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.store.Store;

@Controller
public class SignOutController {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);
    private static final String SIGN_IN_INFO = SessionKeys.SIGN_IN_INFO.getKey();

    @Autowired
    private IIdentityProvider identityProviders;
    @Autowired
    private Store store;

    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse,
            final HttpServletRequest httpServletRequest) throws IOException {

        LOGGER.debug("Processing sign out");

        final Session chSession = (Session) httpServletRequest
                .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

        if (chSession.getSignInInfo().isSignedIn()) {

            final Map<String, Object> sessionData = chSession.getData();
            LOGGER.debugContext("Sign Out", "Sign Out", sessionData);
            final Map<String, Object> signInInfo =
                    (Map<String, Object>) sessionData.get(SIGN_IN_INFO);

            signInInfo.replace(SessionKeys.SIGNED_IN.getKey(), 1, 0);
            sessionData.remove(SIGN_IN_INFO);

            final String zxsKey = (String) sessionData.get(".zxs_key");// This is the id of cookie
                                                                       // stored in redis
            if (zxsKey != null) {
                LOGGER.trace("Deleting ZXS info from cache");
                store.delete(zxsKey);
            }
        }
        httpServletResponse.sendRedirect(identityProviders.getRedirectUriPage());
    }

}
