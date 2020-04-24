package uk.gov.ch.developer.docs.controller.developer;

import static uk.gov.companieshouse.session.handler.SessionHandler.buildSessionCookie;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private IIdentityProvider identityProvider;

    @Autowired
    private IOauth oauth;

    @Autowired
    private SessionFactory sessionFactory;

    public void getCallback(@RequestParam("state") String state, @RequestParam("code") String code,
            final HttpServletResponse httpServletResponse) {
        try {
            final boolean valid = oauth.isValid(state, code);
            if (valid) {
                httpServletResponse.sendRedirect(identityProvider.getRedirectUriPage());
                LOGGER.debug("Original Session ID: " + chSession.getCookieId());
                final Map<String, Object> originalSessionData = chSession.getData();

                Session session = sessionFactory
                        .regenerateSession(chSession.getCookieId(), originalSessionData, chSession);
                httpServletResponse.addCookie(buildSessionCookie(session));
                UserProfileResponse userProfileResponse = oauth.getUserProfile(session, oauthTokenResponse);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (final Exception e) {
            LOGGER.error(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
