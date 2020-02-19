package uk.gov.ch.developer.docs.session;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionImpl;
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

    private SessionFactory() {
    }

    public SessionImpl getSessionByCookieId(final Store store, final String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(store, cookieId);
    }

    public Session getSessionByCookieId(final String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(cookieId);
    }

    public Session createSession() {
        return getSessionByCookieId(null);
    }

}
