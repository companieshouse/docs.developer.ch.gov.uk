package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.ch.oauth.IOauth;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;


@ExtendWith(MockitoExtension.class)
public class SignInControllerTest {

    public static final String AUTHORISE_URI = "https://example.com/authorise";
    static final StringBuffer REQUEST_URL_STRING_BUFFER = new StringBuffer(
            "https://www.example.com");
    static final String EMAIL = "email@example.com";
    static final String NONCE = "nonce";
    static final String ORIGINAL_REQUEST_URI = "https://example.com/original";
    static final String SCOPE = "scope";
    static final String STATE = "state";
    static final String AUTH_URL = "https://example.com/auth";
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
    @Spy
    @InjectMocks
    private SignInController signInController;

    void setup() {
        doReturn(NONCE).when(signInController).generateSessionNonce(any());
        when(request.getRequestURL()).thenReturn(REQUEST_URL_STRING_BUFFER);
        when(oauth.oauth2EncodeState(REQUEST_URL_STRING_BUFFER.toString(), NONCE, "content"))
                .thenReturn(STATE);
        when(identityProvider.getAuthorisationUrl(STATE)).thenReturn(AUTHORISE_URI);
    }

    @Test
    void getSignInTest() throws IOException {
        setup();
        when(request.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY)).thenReturn(session);

        signInController.getSignIn(request, response);

        verify(signInController).redirectForAuth(session, request, response);
    }

    @Test
    void redirectForAuthTest() throws IOException {
        setup();

        signInController.redirectForAuth(session, request, response);

        verify(response).sendRedirect(AUTHORISE_URI);
    }

    @Test
    void createAuthoriseURIWithForceAndHintTest() {
        final String hint = "hint value";
        when(oauth.oauth2EncodeState(EMAIL, NONCE, "email"))
                .thenReturn(hint);
        when(identityProvider.getAuthorisationUrl(ORIGINAL_REQUEST_URI, SCOPE))
                .thenReturn(AUTH_URL);

        final String authoriseURIWithForceAndHint = signInController
                .createAuthoriseURIWithForceAndHint(ORIGINAL_REQUEST_URI, SCOPE, NONCE, EMAIL);
        assertEquals(AUTH_URL + "&reauthenticate=force&hint=" + hint, authoriseURIWithForceAndHint);
    }
}
