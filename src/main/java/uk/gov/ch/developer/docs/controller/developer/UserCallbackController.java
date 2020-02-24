package uk.gov.ch.developer.docs.controller.developer;

import com.nimbusds.jose.Payload;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.IdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    @Autowired
    private SessionService sessionService;
    @Autowired
    private IdentityProvider identityProvider;
    @Autowired
    private IOauth oauth;

    @GetMapping
    @ResponseBody
    public String getCallback(@RequestParam("state") String state,
            @RequestParam("code") String code) {
        //LOGGER.info("Code:"+code);
        //LOGGER.info("State:"+state);

        //sessionService.getSessionDataFromContext();

        final Payload payload = oauth.oauth2DecodeState(state);
        final JSONObject jsonObject = payload.toJSONObject();
        final String returnedNonce = jsonObject.getAsString("nonce");
        LOGGER.info("Nonce:" + returnedNonce);
        if (!oauth.oauth2VerifyNonce(returnedNonce)) {
            LOGGER.error("Invalid nonce value in state during oauth2 callback");
        }
        return "Found";
    }

}
