package uk.gov.ch.oauth;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.store.Store;

@ExtendWith(MockitoExtension.class)
public class Oauth2Test {

    @Mock
    private Store store;
    @Mock
    private Session session;

    private final SignInInfo signInInfo = new SignInInfo();

    @InjectMocks
    private Oauth2 oauth2;

    @Test
    @DisplayName("Test that a valid signed in user's session state is correctly altered")
    public void testInvalidateSessionWhenSignedIn() throws IOException {
        final String zxsValue = "0000000001z";
        signInInfo.setSignedIn(true);
        when(session.getSignInInfo()).thenReturn(signInInfo);

        Map<String, Object> data = setUserSessionData(zxsValue);
        when(session.getData()).thenReturn(data);

        oauth2.invalidateSession(session, SessionKeys.SIGN_IN_INFO.getKey(), store);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));

        verify(store, only()).delete(zxsValue);

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
