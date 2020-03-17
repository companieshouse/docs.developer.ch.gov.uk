package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.nimbusds.jose.Payload;
import net.minidev.json.JSONObject;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserCallbackControllerTest {

    @Mock
    private IOauth oauth;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private Payload payload;
    @Mock
    private JSONObject jsonObject;
    @Mock
    private IIdentityProvider identityProvider;

    @InjectMocks
    private UserCallbackController userCallbackController;

    @Test
    public void testGetCallback() {

        String code = "";
        String state = "";

        when(oauth.oauth2DecodeState(state)).thenReturn(payload);
        when(payload.toJSONObject()).thenReturn(jsonObject);
        when(jsonObject.getAsString("nonce")).thenReturn("");

        assertNotNull(userCallbackController.getCallback(state, code, httpServletRequest));
    }
    
}
