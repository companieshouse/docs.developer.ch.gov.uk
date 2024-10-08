package uk.gov.ch.developer.docs.controller.developer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.ch.oauth.IOauth;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignOutControllerTest {

    private static final String REDIRECT_PAGE = "/home";

    @Mock
    private IOAuthCoordinator ioAuthCoordinator;
    @Mock
    private IOauth iOauth;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Session session;
    @InjectMocks
    private SignOutController signOutController;

    @Test
    @DisplayName("Test that invalidate Session Is Called when signing out")
    void testThatInvalidateSessionIsCalled() throws IOException {
        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(ioAuthCoordinator.getOAuth()).thenReturn(iOauth);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        verify(iOauth).invalidateSession(session);
    }

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    void testDoSignOut() throws IOException {
        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.signOut(httpServletRequest)).thenReturn(REDIRECT_PAGE);
        when(ioAuthCoordinator.getOAuth()).thenReturn(iOauth);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }

}