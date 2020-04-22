package uk.gov.ch.developer.docs.controller.developer;

import javax.servlet.http.HttpServletRequest;
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
    public void getCallback(@RequestParam("state") String state,
            @RequestParam("code") String code, final HttpServletRequest httpServletRequest, final
    HttpServletResponse httpServletResponse) {
        try {
            LOGGER.trace("Code:" + code);
            LOGGER.trace("State:" + state);

            final boolean invalid = !oauth.isValid(state, code);
            if (invalid) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {//
//            LOGGER.debug("Getting User Profile");
//
//            final boolean noProfile = noUserProfile(code);
//            if (noProfile) {
//                // TODO raise error
//                LOGGER.error("No user profile returned in OAuth Callback");
//                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
//                        DUMMY_ERROR_NO_USER_PROFILE_RETURNED);
//                return;
//            }
                httpServletResponse.sendRedirect(
                        identityProvider.getRedirectUriPage());// where sign-in was initiated
            }
        } catch (final Exception e) {
            LOGGER.error(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
