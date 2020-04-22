package uk.gov.ch.developer.docs.controller.developer;

import com.nimbusds.jose.Payload;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    public static final String DUMMY_ERROR_RESULT_MISMATCHED_NONCES = "Dummy Error Result:Mismatched Nonces";
    public static final String DUMMY_ERROR_NO_USER_PROFILE_RETURNED = "Dummy Error: No User Profile Returned";
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private IIdentityProvider identityProvider;

    @Autowired
    private IOauth oauth;

    @GetMapping
    public void getCallback(@RequestParam("state") String state,
            @RequestParam("code") String code, final HttpServletRequest httpServletRequest, final
    HttpServletResponse httpServletResponse) throws IOException {
        try {
            LOGGER.trace("Code:" + code);
            LOGGER.trace("State:" + state);
            final Session chSession = (Session) httpServletRequest
                    .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

            final String returnedNonce = getNonceFromState(state);
            if (!oauth.oauth2VerifyNonce(returnedNonce)) {
                LOGGER.error("Invalid nonce value in state during oauth2 callback");
                // return "redirect:/"; TODO redirect will not work, needs to be addressed for unmatched
                // Nonce values
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        DUMMY_ERROR_RESULT_MISMATCHED_NONCES);
                return;
            }

            LOGGER.debug("Getting User Profile");

            UserProfileResponse userProfileResponse = oauth.getUserProfile(code, chSession);

            if (userProfileResponse == null) {
                // TODO raise error
                LOGGER.error("No user profile returned in OAuth Callback");
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        DUMMY_ERROR_NO_USER_PROFILE_RETURNED);
                return;
            }
            httpServletResponse.sendRedirect(identityProvider.getRedirectUriPage());
            // where sign-in was initiated
        } catch (final Exception e) {
            LOGGER.error(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    String getNonceFromState(final String state) {
        final Payload payload = oauth.oauth2DecodeState(state);
        final JSONObject jsonObject = payload.toJSONObject();
        return jsonObject.getAsString("nonce");
    }

}
