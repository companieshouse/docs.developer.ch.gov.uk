package uk.gov.ch.oauth.session;

import java.util.Map;
<<<<<<< HEAD:src/main/java/uk/gov/ch/developer/docs/session/SessionFactory.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionImpl;
=======
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;
>>>>>>> origin/feature/user-oauth/ci:src/main/java/uk/gov/ch/oauth/session/SessionFactory.java
import uk.gov.companieshouse.session.store.Store;


/**
 * This is a wrapper component for {@link uk.gov.companieshouse.session.SessionFactory} that allows
 * for mocking return results, rather than the implemented static methods.
 *
 * @see uk.gov.companieshouse.session.SessionFactory#getSessionByCookieId(String)
 * @see uk.gov.companieshouse.session.SessionFactory#getSessionByCookieId(Store, String)
 */
@Component
public class SessionFactory {
    
    @Autowired
    Store store;

    public Map<String, Object> getSessionDataFromContext() {
        return SessionHandler.getSessionDataFromContext();
    }

    Session getSessionByCookieId(final String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(cookieId);
    }

    public Session createSession() {
        return getSessionByCookieId(null);
    }
    
    public Session regenerateSession(final String cookieId, Map<String, Object> data) {
        return new SessionImpl(store, cookieId, data);
    }

}
