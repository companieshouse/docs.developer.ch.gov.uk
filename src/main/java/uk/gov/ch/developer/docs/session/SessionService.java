package uk.gov.ch.developer.docs.session;

import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Component
public class SessionService {

    public Map<String, Object> getSessionDataFromContext() {

        return SessionHandler.getSessionDataFromContext();
    }
}