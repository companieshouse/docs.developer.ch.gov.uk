package uk.gov.ch.developer.docs.controller.developer;

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
import uk.gov.ch.oauth.IOAuthCoordinator;


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
    IOAuthCoordinator coordinator;
    @Spy
    @InjectMocks
    private SignInController signInController;

    @Test
    void doSignInTestToEnsureThatAUserIsSentToTheAuthorisePage() throws IOException {
        when(coordinator.getAuthoriseUriFromRequest(request)).thenReturn(AUTHORISE_URI);
        signInController.doSignIn(request, response);
        verify(response).sendRedirect(AUTHORISE_URI);
    }

}
