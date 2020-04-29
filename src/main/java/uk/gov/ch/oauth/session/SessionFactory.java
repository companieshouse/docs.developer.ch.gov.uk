package uk.gov.ch.oauth.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.store.Store;

import java.util.Map;

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

    public static Session getSessionByCookieId(Store store, String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(store, cookieId);
    }

    Session getSessionByCookieId(final String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(cookieId);
    }

    public Session createSession() {
        return getSessionByCookieId(null);
    }

    public Session getSessionFromContext() {
        return SessionHandler.getSessionFromContext();
    }

    public Session regenerateSession(final Session chSession) {
        final Map<String, Object> originalSessionData = chSession.getData();
        chSession.clear();
        final Session session = getSessionByCookieId(store, chSession.getCookieId());
        session.setData(originalSessionData);
        return session;
    }
}
