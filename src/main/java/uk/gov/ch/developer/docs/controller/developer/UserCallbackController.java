package uk.gov.ch.developer.docs.controller.developer;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import com.nimbusds.jose.Payload;
import net.minidev.json.JSONObject;
import reactor.core.publisher.Mono;
import uk.gov.ch.developer.docs.DocsWebApplication;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private IIdentityProvider identityProvider;

    @Autowired
    private IOauth oauth;

    private final WebClient webClient = WebClient.create();
    private final Duration duration = Duration.ofSeconds(10L);

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

        LOGGER.debug("Getting User Profile....");

        OAuthToken oauthToken = getOAuthToken(code);
        UserProfileResponse userProfileResponse = getUserProfile(oauthToken);

        if (userProfileResponse != null) {

            LOGGER.debug("Processing Response");
            oauthToken.setAccessToken(signInData);
            userProfileResponse.setUserProfile(signInData);
            
            signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);
            chSession.getData().put(SessionKeys.SIGN_IN_INFO.getKey(), signInData);

        }

        LOGGER.debug("Redirecting...");

        return ("redirect:" + identityProvider.getRedirectUriPage());// TODO redirect back to page
                                                                     // where sign-in was initiated
    }

    private UserProfileResponse getUserProfile(OAuthToken tokenResponse) {
        LOGGER.debug("Requesting User Profile");
        
        final URI profileUrl = URI.create(identityProvider.getProfileUrl());
        
        return webClient.get()
                .uri(profileUrl)
                .headers(h -> h.setBearerAuth(tokenResponse.getToken()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.bodyToMono(UserProfileResponse.class))
                .block(duration);
    }

    private OAuthToken getOAuthToken(String code) {
        LOGGER.debug("Getting OAuth Token");
        
        final URI tokenUrl = URI.create(identityProvider.getTokenUrl());
        final Mono<String> postRequest = Mono.just(identityProvider.getPostRequestBody(code));
        final BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters
        .fromPublisher(postRequest, String.class);
        
        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(bodyInserter)
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .block(duration);
    }

    private String getNonceFromState(final String state) {
        final Payload payload = oauth.oauth2DecodeState(state);
        final JSONObject jsonObject = payload.toJSONObject();
        return jsonObject.getAsString("nonce");
    }

}
