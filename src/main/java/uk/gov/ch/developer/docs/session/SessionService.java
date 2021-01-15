package uk.gov.ch.developer.docs.session;

import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Service
public class SessionService {

    public Map<String, Object> getSessionDataFromContext() {
        return SessionHandler.getSessionDataFromContext();
    }

    public Session getSessionFromContext() {
        return SessionHandler.getSessionFromContext();
    }
}