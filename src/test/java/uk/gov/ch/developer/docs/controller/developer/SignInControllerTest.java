package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private SignInController signInController;
    String requestUrl = "http://requesturl";
    String queryString = "querystring";
    String originalRequestUri = "originalRequestUri";
    String scope = "scope";
    String nonce = "nonce";
    String email = "email@email";


    @Test
    void getSignInTest() throws IOException {
        signInController.getSignIn(request, response);

        verify(request, times(1)).getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

    }

    @Test
    void redirectForAuthTest() throws IOException {
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
