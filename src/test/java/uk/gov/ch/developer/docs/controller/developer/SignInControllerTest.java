package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignInControllerTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    IOauth oauth;
    @Mock
    IIdentityProvider identityProvider;
    @Mock
    Session session;

    @InjectMocks
    private SignInController signInController;
    StringBuffer requestUrlStringBuffer = new StringBuffer("http://requesturl");
    String originalRequestUri = "originalRequestUri";
    String scope = "scope";
    String nonce = "nonce";
    String email = "email@email";


    @Test
    void getSignInTest() throws IOException {
        when(request.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY)).thenReturn(session);
        when(request.getRequestURL()).thenReturn(requestUrlStringBuffer);
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class))).thenReturn("state");
        when(identityProvider.getAuthorisationUrl("state")).thenReturn("authoriseUri");

        signInController.getSignIn(request, response);

        verify(request, times(1)).getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);
        verify(response, times(1)).sendRedirect(any(String.class));
    }

    @Test
    void redirectForAuthTest() throws IOException {
        when(request.getRequestURL()).thenReturn(requestUrlStringBuffer);
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class))).thenReturn("state");
        when(identityProvider.getAuthorisationUrl("state")).thenReturn("authoriseUri");

        signInController.redirectForAuth(session, request, response);

        verify(oauth, times(1)).oauth2EncodeState(any(String.class), any(String.class), any(String.class));
        verify(identityProvider, times(1)).getAuthorisationUrl(any(String.class));
        verify(response, times(1)).sendRedirect(any(String.class));

    }

    @Test
    void createAuthoriseURIWithForceAndHintTest() {
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class))).thenReturn("hint");
        when(identityProvider.getAuthorisationUrl(any(String.class), any(String.class))).thenReturn("authUrl");

        Assert.assertEquals(signInController.createAuthoriseURIWithForceAndHint(originalRequestUri, scope, nonce, email), "authUrl&reauthenticate=force&hint=hint");
    }
}
