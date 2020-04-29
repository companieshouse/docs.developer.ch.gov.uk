package uk.gov.ch.oauth.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionImpl;
import uk.gov.companieshouse.session.store.Store;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SessionFactoryTest {

    private static final String COOKIE_ID = "OldSessionID" + "signature";

    @Mock
    private Store store;

    @InjectMocks
    SessionFactory sessionFactory;

    @Spy
    Session oldSession = new SessionImpl();

    @Test
    public void testRegenerateSession() {
        oldSession.setCookieId(COOKIE_ID);

        Map<String, Object> data = new HashMap<>();
        data.put(".zxs", "1234567890");
        data.put(".id", "OldSessionID");

        oldSession.setData(data);

        Session newSession = sessionFactory.regenerateSession();

        verify(oldSession).clear();

        assertNotEquals(COOKIE_ID, newSession.getCookieId());
        assertNotEquals("OldSessionID", newSession.getData().get(".id"));
        assertEquals("1234567890", newSession.getData().get(".zxs"));
        assertEquals(2, newSession.getData().size());
    }
}
