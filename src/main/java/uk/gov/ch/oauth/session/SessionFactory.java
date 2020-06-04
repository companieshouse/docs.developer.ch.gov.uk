package uk.gov.ch.oauth.session;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;
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

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    public Map<String, Object> getSessionDataFromContext() {
        return SessionHandler.getSessionDataFromContext();
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

    public Session regenerateSession() {
        final Session chSession = getSessionFromContext();

        LOGGER.debug("Original Session ID: " + chSession.getCookieId());
        final Map<String, Object> originalSessionData = chSession.getData();

        chSession.clear();

        final Session session = getSessionByCookieId(chSession.getCookieId());
        session.setData(originalSessionData);
        return session;
    }

    public Store getDefaultStore() {
        return uk.gov.companieshouse.session.SessionFactory.getDefaultStoreImpl();
    }
}
