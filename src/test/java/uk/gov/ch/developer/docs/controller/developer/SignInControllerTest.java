package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;


@ExtendWith(MockitoExtension.class)
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
    final static StringBuffer requestUrlStringBuffer = new StringBuffer("https://www.example.com");
    final static String originalRequestUri = "originalRequestUri";
    final static String scope = "scope";
    final static String nonce = "nonce";
    final static String email = "email@example.com";
    @InjectMocks
    private SignInController signInController;

    @Test
    void getSignInTest() throws IOException {
        when(request.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY)).thenReturn(session);
        when(request.getRequestURL()).thenReturn(requestUrlStringBuffer);
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class)))
                .thenReturn("state");
        when(identityProvider.getAuthorisationUrl("state")).thenReturn("authoriseUri");

        signInController.getSignIn(request, response);

        verify(request).getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);
        verify(response).sendRedirect(any(String.class));
    }

    @Test
    void redirectForAuthTest() throws IOException {
        when(request.getRequestURL()).thenReturn(requestUrlStringBuffer);
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class)))
                .thenReturn("state");
        when(identityProvider.getAuthorisationUrl("state")).thenReturn("authoriseUri");

        signInController.redirectForAuth(session, request, response);

        verify(oauth)
                .oauth2EncodeState(any(String.class), any(String.class), any(String.class));
        verify(identityProvider).getAuthorisationUrl(any(String.class));
        verify(response).sendRedirect(any(String.class));

    }

    @Test
    void createAuthoriseURIWithForceAndHintTest() {
        when(oauth.oauth2EncodeState(any(String.class), any(String.class), any(String.class)))
                .thenReturn("hint");
        when(identityProvider.getAuthorisationUrl(any(String.class), any(String.class)))
                .thenReturn("authUrl");

        Assert.assertEquals("authUrl&reauthenticate=force&hint=hint", signInController
                .createAuthoriseURIWithForceAndHint(originalRequestUri, scope, nonce, email));
    }
}
