package uk.gov.ch.oauth;

import java.util.Map;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;


/**
 * Groups and wraps funtions from the CH Session methods.
 */
class SessionUtils {

    private SessionUtils() {
        //Hiding constructor to prevent initialisation.
    }

    static Map<String, Object> getSessionDataFromContext() {
        return SessionHandler.getSessionDataFromContext();
    }

    static Session getSessionByCookieId(final String cookieId) {
        return uk.gov.companieshouse.session.SessionFactory.getSessionByCookieId(cookieId);
    }

    static Session createSession() {
        return getSessionByCookieId(null);
    }
}
