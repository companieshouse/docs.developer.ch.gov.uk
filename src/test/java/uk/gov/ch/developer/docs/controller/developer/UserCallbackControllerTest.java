package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import com.nimbusds.jose.Payload;
import javax.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;

@ExtendWith(MockitoExtension.class)
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
    @Spy
    private UserCallbackController userCallbackController;

    @Test
    @DisplayName("Checking result when the returned nonce does not match the one for the current session")
    public void testGetCallbackBadNonce() {
        final String code = "dummy Code";
        final String state = "dummy State";
        doReturn("bad state nonce").when(userCallbackController).getNonceFromState(state);

        final String callbackResult = userCallbackController
                .getCallback(state, code, httpServletRequest);

        assertEquals(UserCallbackController.DUMMY_ERROR_RESULT_MISMATCHED_NONCES, callbackResult);
    }

    @Test
    void getCallback() {
    }
}
