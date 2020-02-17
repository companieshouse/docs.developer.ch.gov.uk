package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @Autowired
    private SessionService sessionService;

    private String base64Key;

    public UserCallbackController(EnvironmentReader reader) {
        this.base64Key = reader.getMandatoryString("OAUTH2_REQUEST_KEY");
    }

    @GetMapping
    public void getParams(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, @RequestParam("state") String state,
            @RequestParam("code") String code) throws ParseException, JOSEException,
            net.minidev.json.parser.ParseException, IOException {

        Session sessionCallback = sessionService.getSessionFromContext();

        Map<String, Object> sessionMap = sessionCallback.getData();
        String nonceSession = sessionMap.get(SessionKeys.NONCE.getKey()).toString();

        String nonceState = decryptState(state);

        if (nonceState.equals(nonceSession)) {
            LOGGER.info("Nonce values are equal");
        }

        httpServletResponse.sendRedirect("http://dev.chs-dev.internal:4904/");
    }

    private String decryptState(String state)
            throws ParseException, JOSEException, net.minidev.json.parser.ParseException {

        JWEObject jweObject = JWEObject.parse(state);

        byte[] key = Base64.decodeBase64(base64Key);
        jweObject.decrypt(new DirectDecrypter(key));

        Payload payload = jweObject.getPayload();

        JSONObject json = (JSONObject) JSONValue.parseWithException(payload.toString());
        String nonceState = (String) json.get("nonce");
        return nonceState;
    }

}
