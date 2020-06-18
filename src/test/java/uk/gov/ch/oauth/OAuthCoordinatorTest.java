package uk.gov.ch.oauth;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.ch.oauth.identity.IIdentityProvider;


@ExtendWith(MockitoExtension.class)
class OAuthCoordinatorTest {

    static final String STATE = "state";

    @Spy
    @InjectMocks
    OAuthCoordinator oAuthCoordinator;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private HttpServletRequest request;
    @Mock
    private IIdentityProvider mockIdentityProvider;
    @Mock
    private IOauth mockOauth;

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
    @DisplayName("getAuthoriseUriFromRequestTest")
    void getAuthoriseUriFromRequestTest() {
        when(oAuthCoordinator.getOAuth()).thenReturn(mockOauth);
        when(mockOauth.prepareState(request)).thenReturn(STATE);
        when(oAuthCoordinator.getIdentityProvider()).thenReturn(mockIdentityProvider);
        oAuthCoordinator.getAuthoriseUriFromRequest(request);
        verify(mockIdentityProvider).getAuthorisationUrl(STATE);
    }

}