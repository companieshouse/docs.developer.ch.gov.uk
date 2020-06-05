package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IdentityProvider;

@ExtendWith(MockitoExtension.class)
public class UserCallbackControllerTest {

    public static final String HTTP_EXAMPLE_COM_REDIRECT = "http://example.com/redirect";
    private static final String ERROR = "mock_error";
    private final String STATE = "dummy State";
    private final String CODE = "dummy Code";

    @Mock
    IdentityProvider identityProvider;
    @Mock
    private HttpServletResponse servletResponse;
    @Mock
    private Oauth2 oauth2;
    @Mock
    private HttpServletResponse mockResponse;

    @InjectMocks
    private UserCallbackController userCallbackController;

    @Test
    @DisplayName("Checking result when state or code are invalid")
    public void testGetCallbackInvalidCodeState() throws IOException {

        doReturn(false).when(oauth2).validate(STATE, CODE, servletResponse);

        userCallbackController
                .getCallback(STATE, CODE, servletResponse);
        verify(servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoMoreInteractions(servletResponse);
    }

    @Test
    @DisplayName("Checking result when state or code are invalid")
    public void testGetCallbackException() {

        doThrow(new RuntimeException("bad connection")).when(oauth2)
                .validate(STATE, CODE, servletResponse);

        userCallbackController.getCallback(STATE, CODE, servletResponse);
        verify(servletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verifyNoMoreInteractions(servletResponse);
    }

    @Test
    @DisplayName("Checking result when state or code are valid")
    public void testGetCallbackSuccess() throws IOException {
        when(identityProvider.getRedirectUriPage()).thenReturn(HTTP_EXAMPLE_COM_REDIRECT);

        doReturn(true).when(oauth2).validate(STATE, CODE, servletResponse);

        userCallbackController
                .getCallback(STATE, CODE, servletResponse);
        verify(servletResponse).sendRedirect(HTTP_EXAMPLE_COM_REDIRECT);
        verifyNoMoreInteractions(servletResponse);
    }

    @Test
    @DisplayName("Checking access refused sends forbidden error with reason.")
    public void accessRefused_Escalates403Error_test() throws IOException {
        userCallbackController.accessRefused(ERROR, mockResponse);
        verify(mockResponse, only()).sendError(403, ERROR);
    }
}
