package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignOutControllerTest {

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
    private Map<String, Object> data;

    @InjectMocks
    private SignOutController signOutController;

    @Test
    @DisplayName("Test that a valid signed in user's session state is correctly altered")
    public void testDoSignOutWhenSignedIn() throws IOException {
        final String zxsValue = "0000000001z";
        SignInInfo signInInfo = new SignInInfo();
        signInInfo.setSignedIn(true);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        when(session.getData()).thenReturn(setUserSessionData(zxsValue));

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));

        verify(store).delete(zxsValue);
        verify(httpServletResponse).sendRedirect(identityProviders.getRedirectUriPage());
    }

    @Test
    @DisplayName("Test that a user not signed in is unable to alter session state and is correctly redirected back home")
    public void testDoSignOutWhenUserIsNotSignedIn() throws IOException {
        SignInInfo signInInfo = new SignInInfo();
        signInInfo.setSignedIn(false);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        verify(session, times(0)).getData();
        verify(httpServletResponse).sendRedirect(identityProviders.getRedirectUriPage());
    }

    @Test
    @DisplayName("Test that a valid signed in user's without a valid cookie is unable to delete from store")
    public void testSignOutWhenUserHasInvalidZXSKey() throws IOException {
        final String zxsValue = null;
        SignInInfo signInInfo = new SignInInfo();
        signInInfo.setSignedIn(true);

        when(httpServletRequest.getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY))
                .thenReturn(session);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        when(session.getData()).thenReturn(setUserSessionData(zxsValue));

        signOutController.doSignOut(httpServletResponse, httpServletRequest);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));

        verify(store, times(0)).delete(zxsValue);
        verify(httpServletResponse).sendRedirect(identityProviders.getRedirectUriPage());
    }

    private Map<String, Object> setUserSessionData(String zxsValue) {
        data = new HashMap<>();
        Map<String, Object> signInData = new HashMap<>();

        signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);

        data.put(SessionKeys.SIGN_IN_INFO.getKey(), signInData);
        data.put(".zxs_key", zxsValue);

        return data;
    }

}
