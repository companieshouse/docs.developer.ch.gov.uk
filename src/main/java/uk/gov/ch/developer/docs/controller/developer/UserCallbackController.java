package uk.gov.ch.developer.docs.controller.developer;

import java.text.ParseException;
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
    public String getParams(@RequestParam("state") String state, @RequestParam("code") String code)
            throws ParseException, KeyLengthException, JOSEException,
            net.minidev.json.parser.ParseException {
        LOGGER.info("State: " + state);
        LOGGER.info("Code: " + code);

        sessionService.getSessionDataFromContext();

        JWEObject jweObject = JWEObject.parse(state);

        byte[] key = Base64.decodeBase64(base64Key);
        jweObject.decrypt(new DirectDecrypter(key));

        Payload payload = jweObject.getPayload();

        JSONObject json = (JSONObject) JSONValue.parseWithException(payload.toString());
        String nonce = (String) json.get("nonce");

        return "Found";
    }

}
