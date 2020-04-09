package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
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

    private final SignInInfo signInInfo = new SignInInfo();

    @InjectMocks
    private SignOutController signOutController;

    @Test
    @DisplayName("Test that a valid signed in user's session state is correctly altered")
    public void testDoSignOutWhenSignedIn() throws IOException {
        final String zxsValue = "0000000001z";

        signInInfo.setSignedIn(true);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);

        Map<String, Object> data = setUserSessionData(zxsValue);

        when(session.getData()).thenReturn(data);
        when(identityProviders.getRedirectUriPage()).thenReturn(REDIRECT_PAGE);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));

        verify(store, only()).delete(zxsValue);
        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }

    @Test
    @DisplayName("Test that a user not signed in is unable to alter session state and is correctly redirected back home")
    public void testDoSignOutWhenUserIsNotSignedIn() throws IOException {

        signInInfo.setSignedIn(false);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        when(identityProviders.getRedirectUriPage()).thenReturn(REDIRECT_PAGE);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        verifyNoMoreInteractions(session);
        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }

    @Test
    @DisplayName("Test that a valid signed in user's without a valid cookie is unable to delete from store")
    public void testSignOutWhenUserHasInvalidZXSKey() throws IOException {
        final String zxsValue = null;

        signInInfo.setSignedIn(true);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);

        Map<String, Object> data = setUserSessionData(zxsValue);

        when(session.getData()).thenReturn(data);
        when(identityProviders.getRedirectUriPage()).thenReturn(REDIRECT_PAGE);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));

        verifyNoMoreInteractions(session);
        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }

    private Map<String, Object> setUserSessionData(String zxsValue) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> signInData = new HashMap<>();

        signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);

        data.put(SessionKeys.SIGN_IN_INFO.getKey(), signInData);
        data.put(".zxs_key", zxsValue);

        return data;
    }

}
