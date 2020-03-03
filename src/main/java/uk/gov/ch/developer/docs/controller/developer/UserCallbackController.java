package uk.gov.ch.developer.docs.controller.developer;

import java.net.URI;
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
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    @Autowired
    IIdentityProvider identityProvider;
    @Autowired
    private IOauth oauth;

    @GetMapping
    public String getCallback(@RequestParam("state") String state,
            @RequestParam("code") String code) {
        LOGGER.trace("Code:" + code);
        LOGGER.trace("State:" + state);

        final String returnedNonce = getNonceFromState(state);
        if (!oauth.oauth2VerifyNonce(returnedNonce)) {
            LOGGER.error("Invalid nonce value in state during oauth2 callback");
            // return "redirect:/"; TODO redirect will not work, needs to be addressed for unmatched
            // Nonce values
        }

        WebClient webClient = WebClient.create();

        webClient.post().uri(URI.create(identityProvider.getTokenUrl()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Mono.just(getPostRequestBody(code)),
                        String.class))
                .retrieve().bodyToMono(OAuthToken.class)
                .subscribe(s -> LOGGER.trace("Access Token: " + s.getAccessToken()));// TODO remove
                                                                                     // this log
                                                                                     // line

        return ("redirect:" + identityProvider.getRedirectUriPage());// TODO redirect back to page
                                                                     // where sign-in was initiated
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
