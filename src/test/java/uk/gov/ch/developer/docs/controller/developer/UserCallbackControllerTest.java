package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;

@ExtendWith(MockitoExtension.class)
public class UserCallbackControllerTest {

    public static final String REDIRECT_URL = "redirectURL";

    @Mock
    IOAuthCoordinator mockCoordinator;
    @Mock
    UnauthorisedException mockException;
    @Mock
    HttpServletResponse mockResponse;
    @Mock
    Map<String, String> mockParams;

    @InjectMocks
    private UserCallbackController userCallbackController;

    @Test
    @DisplayName("Returns error page when coordinator errors")
    public void testReturnsErrorWhenCoordinatorThrowsException() throws UnauthorisedException {
        when(mockCoordinator.getPostCallbackRedirectURL(any(HttpServletResponse.class), anyMap()))
                .thenThrow(mockException);
        String ret = userCallbackController.callback(mockParams, mockResponse);
        assertEquals("error", ret);
        verify(mockCoordinator).getPostCallbackRedirectURL(mockResponse, mockParams);
        verifyNoMoreInteractions(mockCoordinator, mockResponse, mockParams);
    }

    @Test
    @DisplayName("Returns redirect when coordinator errors")
    public void testReturnsRedirectThatCoordinatorReturns() throws UnauthorisedException {
        when(mockCoordinator.getPostCallbackRedirectURL(any(HttpServletResponse.class), anyMap()))
                .thenReturn(REDIRECT_URL);
        String ret = userCallbackController.callback(mockParams, mockResponse);
        assertEquals("redirect:" + REDIRECT_URL, ret);
        verify(mockCoordinator).getPostCallbackRedirectURL(mockResponse, mockParams);
        verifyNoMoreInteractions(mockCoordinator, mockResponse, mockParams);
    }

//    @Test
//    @DisplayName("Checking result when state or code are invalid")
//    public void testGetCallbackInvalidCodeState() throws IOException {
//
//        doReturn(false).when(oauth2).validate(STATE, CODE, servletResponse);
//
////        userCallbackController
////                .getCallback(STATE, CODE, servletResponse);
//        verify(servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN);
//        verifyNoMoreInteractions(servletResponse);
//    }
//
//    @Test
//    @DisplayName("Checking result when state or code are invalid")
//    public void testGetCallbackException() {
//
//        doThrow(new RuntimeException("bad connection")).when(oauth2)
//                .validate(STATE, CODE, servletResponse);
//
////        userCallbackController.getCallback(STATE, CODE, servletResponse);
//        verify(servletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        verifyNoMoreInteractions(servletResponse);
//    }
//
//    @Test
//    @DisplayName("Checking result when state or code are valid")
//    public void testGetCallbackSuccess() throws IOException {
//        when(identityProvider.getRedirectUriPage()).thenReturn(HTTP_EXAMPLE_COM_REDIRECT);
//
//        doReturn(true).when(oauth2).validate(STATE, CODE, servletResponse);
//
////        userCallbackController
////                .getCallback(STATE, CODE, servletResponse);
//        verify(servletResponse).sendRedirect(HTTP_EXAMPLE_COM_REDIRECT);
//        verifyNoMoreInteractions(servletResponse);
//    }
//
//    @Test
//    @DisplayName("Checking access refused sends forbidden error with reason.")
//    public void accessRefused_Escalates403Error_test() throws IOException {
////        userCallbackController.accessRefused(ERROR, mockResponse);
//        verify(mockResponse, only()).sendError(403, ERROR);
//    }
}
