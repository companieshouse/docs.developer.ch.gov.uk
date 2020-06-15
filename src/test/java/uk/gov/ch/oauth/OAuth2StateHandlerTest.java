package uk.gov.ch.oauth;

import static com.nimbusds.jose.JWEObject.parse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import java.text.ParseException;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class OAuth2StateHandlerTest {

    private static final String ATTRIBUTE_NAME = "ATR";
    private static final String NONCE = "NON";
    private static final String RETURN_URI = "RET";
    private static final byte[] REQUEST_KEY = new byte[32];
    @InjectMocks
    OAuth2StateHandler stateHandler;
    @Mock
    private IIdentityProvider mockIdentityProvider;
    @Mock
    private Logger mockLogger;

    @Test
    @DisplayName("Encoded string contains correct number of segments.")
    void oauth2EncodeState() {
        when(mockIdentityProvider.getRequestKey()).thenReturn(REQUEST_KEY);

        String encoded = stateHandler.oauth2EncodeState(RETURN_URI, NONCE, ATTRIBUTE_NAME);

        String[] encodedArray = encoded.split("\\.");

        assertEquals(5, encodedArray.length);

        try {
            JWEObject jweObject = parse(encoded);
        } catch (ParseException e) {
            fail("Encoded state cannot be parsed.");
        }
        verifyNoInteractions(mockLogger);
    }

    @Test
    @DisplayName("Encoded string can be decoded.")
    void oauth2DecodeState() {
        when(mockIdentityProvider.getRequestKey()).thenReturn(REQUEST_KEY);

        String encoded = stateHandler.oauth2EncodeState(RETURN_URI, NONCE, ATTRIBUTE_NAME);

        Payload decoded = stateHandler.oauth2DecodeState(encoded);
        JSONObject decodedJson = decoded.toJSONObject();

        assertEquals(RETURN_URI, decodedJson.get(ATTRIBUTE_NAME));
        assertEquals(NONCE, decodedJson.get("nonce"));
    }

    @Test
    @DisplayName("Other Constructor does not error.")
    void loggerFromEnvironment_constructorTest() {

        assertDoesNotThrow(() -> {
            OAuth2StateHandler s = new OAuth2StateHandler(mockIdentityProvider);
        });
    }
}