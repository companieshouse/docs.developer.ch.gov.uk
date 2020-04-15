package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.developer.docs.session.SessionFactory;
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
    @Mock
    SessionFactory sessionFactory;
    static final StringBuffer REQUEST_URL_STRING_BUFFER = new StringBuffer(
            "https://www.example.com");
    static final String EMAIL = "email@example.com";
    static final String NONCE = "nonce";
    static final String ORIGINAL_REQUEST_URI = "originalRequestUri";
    static final String SCOPE = "scope";
    static final String STATE = "state";

    @Spy
    @InjectMocks
    private SignInController signInController;

    void setup() {
        //when(sessionFactory.createSession()).thenReturn(session);
        //request.setAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY, session);
        doReturn(NONCE).when(signInController).generateSessionNonce(any());
        when(request.getRequestURL()).thenReturn(REQUEST_URL_STRING_BUFFER);
        when(oauth.oauth2EncodeState(REQUEST_URL_STRING_BUFFER.toString(), NONCE, "content"))
                .thenReturn(STATE);
        when(identityProvider.getAuthorisationUrl(STATE)).thenReturn("authoriseUri");
    }

    @Test
    void getSignInTest() throws IOException {
        setup();
        when(request.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY)).thenReturn(session);

        signInController.getSignIn(request, response);

        verify(request).getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);
        verify(signInController).redirectForAuth(session, request, response);
    }

    @Test
    void redirectForAuthTest() throws IOException {
        setup();

        signInController.redirectForAuth(session, request, response);

        verify(oauth)
                .oauth2EncodeState(REQUEST_URL_STRING_BUFFER.toString(), NONCE, "content");
        verify(identityProvider).getAuthorisationUrl(STATE);
        verify(response).sendRedirect("authoriseUri");

    }

    @Test
    void createAuthoriseURIWithForceAndHintTest() {
        when(oauth.oauth2EncodeState(EMAIL, NONCE, "email"))
                .thenReturn("hint");
        when(identityProvider.getAuthorisationUrl(ORIGINAL_REQUEST_URI, SCOPE))
                .thenReturn("authUrl");

        Assert.assertEquals("authUrl&reauthenticate=force&hint=hint", signInController
                .createAuthoriseURIWithForceAndHint(ORIGINAL_REQUEST_URI, SCOPE, NONCE, EMAIL));
    }
}
