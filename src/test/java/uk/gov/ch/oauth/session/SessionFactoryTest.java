package uk.gov.ch.oauth.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionImpl;
import uk.gov.companieshouse.session.store.Store;

@ExtendWith(MockitoExtension.class)
public class SessionFactoryTest {

    private static final String COOKIE_ID = "OldSessionIDsignature";
    @Mock
    private final Session oldSession = new SessionImpl();
    @Mock
    private Store store;
    @Mock
    private final Session newSession = new SessionImpl();
    @InjectMocks
    @Spy
    SessionFactory sessionFactory;

    @Test
    public void testRegenerateSession() {
        Map<String, Object> data = new HashMap<>();
        data.put(".zxs", "1234567890");
        data.put(".id", "OldSessionID");

        doReturn(oldSession).when(sessionFactory).getSessionFromContext();
        doReturn(data).when(oldSession).getData();
        doReturn(COOKIE_ID).when(oldSession).getCookieId();
        doReturn(newSession).when(sessionFactory).getSessionByCookieId(COOKIE_ID);

        Session generatedSession = sessionFactory.regenerateSession();

        verify(oldSession).getData();
        verify(oldSession).clear();
        verify(newSession).setData(data);

        assertEquals(newSession, generatedSession);
    }
}
