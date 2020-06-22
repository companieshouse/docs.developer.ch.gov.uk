package uk.gov.ch.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OAuthCoordinatorTest {

    @Spy
    @InjectMocks
    OAuthCoordinator oAuthCoordinator;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private IOauth oauth;

    @Mock
    private Session session;


    @Test
    @DisplayName("Throws Exception if callback params contains error.")
    void getPostCallbackRedirectURLWhenErrorResponse() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("error", "Error Value");

        assertThrows(UnauthorisedException.class,
                () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

        verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Error Value");
    }

    @Test
    @DisplayName("Throws Exception if callback do not contain State.")
    void getPostCallbackRedirectURLWhenMissingValuesState()
            throws UnauthorisedException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("code", "Code");

        assertThrows(UnauthorisedException.class,
                () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

        verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access token");
        verify(oAuthCoordinator, never())
                .validateResponse(anyString(), anyString(), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Throws Exception if callback do not contain Code.")
    void getPostCallbackRedirectURLWhenMissingValuesCode()
            throws UnauthorisedException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put("state", "State");

        assertThrows(UnauthorisedException.class,
                () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

        verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access token");
        verify(oAuthCoordinator, never())
                .validateResponse(anyString(), anyString(), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Test that getSignoutUri returns a value from IdentityProviders getRedirectUri()")
    void testGetSignOutUri() {
        String redirectPage = oAuthCoordinator.getSignoutUri();
        assertEquals("/", redirectPage);
    }

    @Test
    @DisplayName("Test that OAuthCoordinators invalidateSession calls Oauths invalidate session")
    void testOAuthCoordinatorInvalidateSession() {
        when(oAuthCoordinator.getOAuth()).thenReturn(oauth);
        oAuthCoordinator.invalidateSession(session);
        verify(oauth).invalidateSession(session);
    }

}