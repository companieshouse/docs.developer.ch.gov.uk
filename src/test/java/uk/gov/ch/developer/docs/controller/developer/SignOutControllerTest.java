package uk.gov.ch.developer.docs.controller.developer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.Oauth2;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.store.Store;

@ExtendWith(MockitoExtension.class)
public class SignOutControllerTest {

    private static final String REDIRECT_PAGE = "/home";

    @Mock
    private IIdentityProvider identityProviders;
    @Mock
    private Store store;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Session session;
    @Mock
    private Oauth2 oauth2;

    private final SignInInfo signInInfo = new SignInInfo();

    @InjectMocks
    private SignOutController signOutController;

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    public void testDoSignOut() throws IOException {
        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(identityProviders.getRedirectUriPage()).thenReturn(REDIRECT_PAGE);
        signOutController.doSignOut(httpServletResponse, httpServletRequest);
        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }
}
