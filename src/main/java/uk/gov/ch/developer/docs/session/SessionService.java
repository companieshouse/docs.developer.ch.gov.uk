package uk.gov.ch.developer.docs.session;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Service
public class SessionService {

    public Session getSessionFromContext() {
        return SessionHandler.getSessionFromContext();
    }
}