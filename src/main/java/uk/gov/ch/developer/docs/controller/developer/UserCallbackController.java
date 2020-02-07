package uk.gov.ch.developer.docs.controller.developer;

import java.text.ParseException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    protected Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @Autowired
    private SessionService sessionService;

    private static final EnvironmentReader reader = new EnvironmentReaderImpl();

    private static final String base64Key = reader.getMandatoryString("OAUTH2_REQUEST_KEY");

    @GetMapping
    @ResponseBody
    public String getParams(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            @RequestParam("state") String state, @RequestParam("code") String code)
            throws ParseException, KeyLengthException, JOSEException,
            net.minidev.json.parser.ParseException {

        SessionConfig session = new SessionConfig();
        Session sessionCallback = session.getSession(httpServletRequest, httpServletResponse);

        String nonceSession = sessionCallback.getData().get(SessionKeys.NONCE.getKey()).toString();

        // sessionService.getSessionFromContext(); Still returns null, needs to be looked into

        String nonceState = decryptState(state);

        if (nonceState.equals(nonceSession)) {
            LOGGER.info("Nonce values are equal");
        }

        return "/getting-started";
    }

    private String decryptState(String state) throws ParseException, JOSEException,
            KeyLengthException, net.minidev.json.parser.ParseException {

        JWEObject jweObject = JWEObject.parse(state);

        byte[] key = Base64.decodeBase64(base64Key);
        jweObject.decrypt(new DirectDecrypter(key));

        Payload payload = jweObject.getPayload();

        JSONObject json = (JSONObject) JSONValue.parseWithException(payload.toString());
        String nonceState = (String) json.get("nonce");
        return nonceState;
    }

}
