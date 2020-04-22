package uk.gov.ch.developer.docs.controller.developer;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IdentityProvider;

@ExtendWith(MockitoExtension.class)
public class UserCallbackControllerTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    IdentityProvider identityProvider;
    @Mock
    private Oauth2 oauth2;

    @InjectMocks
    @Spy
    private UserCallbackController userCallbackController;

    @Disabled
    @Test
    @DisplayName("Checking result when the returned nonce does not match the one for the current session")
    public void testGetCallbackBadNonce() {
//        final String state = "dummy State";
//        final String code = "dummy Code";
//        doReturn("bad state nonce").when(userCallbackController).getNonceFromState(state);
//
//        final String callbackResult = userCallbackController
//                .getCallback(state, code, httpServletRequest);
//        assertEquals(UserCallbackController.DUMMY_ERROR_RESULT_MISMATCHED_NONCES, callbackResult);
    }

    @Disabled
    @Test
    void getCallback() {
    }
}
