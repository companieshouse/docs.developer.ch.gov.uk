package uk.gov.ch.developer.docs.controller.developer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.nimbusds.jose.Payload;
import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.OAuthToken;
import uk.gov.ch.oauth.UserProfileResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @Autowired
    private IIdentityProvider identityProvider;

    @Autowired
    private IOauth oauth;

    private final WebClient webClient = WebClient.create();

    @GetMapping
    public String getCallback(@RequestParam("state") String state,
            @RequestParam("code") String code, final HttpServletRequest httpServletRequest) {
        LOGGER.trace("Code:" + code);
        LOGGER.trace("State:" + state);

        final String returnedNonce = getNonceFromState(state);
        if (!oauth.oauth2VerifyNonce(returnedNonce)) {
            LOGGER.error("Invalid nonce value in state during oauth2 callback");
            // return "redirect:/"; TODO redirect will not work, needs to be addressed for unmatched
            // Nonce values
        }

        final Session chSession = (Session) httpServletRequest
                .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

        Map<String, Object> signInData = new HashMap<>();

        Mono<OAuthToken> oauthTokenResponse = getOAuthToken(code, webClient);

        LOGGER.debug("Getting User Profile....");

        UserProfileResponse userProfileResponse = getUserProfile(signInData, webClient, oauthTokenResponse);

        if (userProfileResponse != null) {

            LOGGER.debug("Processing Response");
            setUserProfile(signInData, userProfileResponse);
            signInData.put(SessionKeys.SIGNED_IN.getKey(), Integer.valueOf(1));
            chSession.getData().put(SessionKeys.SIGN_IN_INFO.getKey(), signInData);

        }

        LOGGER.debug("Redirecting...");

        return ("redirect:" + identityProvider.getRedirectUriPage());// TODO redirect back to page
                                                                     // where sign-in was initiated
    }

    private UserProfileResponse getUserProfile(Map<String, Object> signInData, WebClient webClient,
            Mono<OAuthToken> oauthTokenResponse) {
        LOGGER.debug("Getting User Profile....");

        OAuthToken oauthToken = oauthTokenResponse.block();

        return requestUserProfile(signInData, webClient, oauthToken);
    }

    private UserProfileResponse requestUserProfile(Map<String, Object> signInData, WebClient webClient,
            OAuthToken tokenResponse) {
        setAccessToken(signInData, tokenResponse);
        return webClient.get().uri(URI.create(identityProvider.getProfileUrl()))
                .headers(h -> h.setBearerAuth(tokenResponse.getToken()))
                .accept(MediaType.APPLICATION_JSON).exchange()
                .flatMap(response -> response.bodyToMono(UserProfileResponse.class))
                .block();
    }

    private Mono<OAuthToken> getOAuthToken(String code, WebClient webClient) {
        LOGGER.debug("Getting OAuth Token....");
        return webClient.post().uri(URI.create(identityProvider.getTokenUrl()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON).body(BodyInserters
                        .fromPublisher(Mono.just(getPostRequestBody(code)), String.class))
                .retrieve()
                .bodyToMono(OAuthToken.class);
    }

    private void setUserProfile(Map<String, Object> signInData, UserProfileResponse userProfileResponse) {
        LOGGER.debug("Setting User Profile...");
        Map<String, Object> userProfileData = new HashMap<>();

        userProfileData.put(SessionKeys.EMAIL.getKey(), userProfileResponse.getEmail());
        userProfileData.put(SessionKeys.USER_ID.getKey(), userProfileResponse.getId());
        userProfileData.put(SessionKeys.LOCALE.getKey(), userProfileResponse.getLocale());
        userProfileData.put(SessionKeys.SCOPE.getKey(), userProfileResponse.getScope());
        userProfileData.put(SessionKeys.FORENAME.getKey(), userProfileResponse.getForename());
        userProfileData.put(SessionKeys.SURNAME.getKey(), userProfileResponse.getSurname());
        userProfileData.put(SessionKeys.PERMISSIONS.getKey(), userProfileResponse.getPermissions());

        LOGGER.debugContext("User Profile Data: ", "setUserProfile() Method", userProfileData);

        signInData.put(SessionKeys.USER_PROFILE.getKey(), userProfileData);
    }

    private void setAccessToken(Map<String, Object> signInData, OAuthToken token) {
        LOGGER.debug("Setting access token...");
        Map<String, Object> accessTokenData = new HashMap<>();

        accessTokenData.put(SessionKeys.ACCESS_TOKEN.getKey(), token.getToken());
        accessTokenData.put(SessionKeys.EXPIRES_IN.getKey(), token.getExpiresIn());
        accessTokenData.put(SessionKeys.REFRESH_TOKEN.getKey(), token.getRefreshToken());
        accessTokenData.put(SessionKeys.TOKEN_TYPE.getKey(), token.getTokenType());

        signInData.put(SessionKeys.ACCESS_TOKEN.getKey(), accessTokenData);
    }

    private String getPostRequestBody(String code) {
        final StringBuilder sb = new StringBuilder();
        sb.append("code=").append(code).append("&client_id=").append(identityProvider.getClientId())
                .append("&client_secret=").append(identityProvider.getClientSecret())
                .append("&redirect_uri=").append(identityProvider.getRedirectUri())
                .append("&grant_type=authorization_code");

        return sb.toString();
    }

    private String getNonceFromState(final String state) {
        final Payload payload = oauth.oauth2DecodeState(state);
        final JSONObject jsonObject = payload.toJSONObject();
        return jsonObject.getAsString("nonce");
    }

}
