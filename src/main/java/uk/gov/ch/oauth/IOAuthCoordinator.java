package uk.gov.ch.oauth;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;

public interface IOAuthCoordinator {

    String getPostCallbackRedirectURL(HttpServletResponse response, Map<String, String> params)
            throws UnauthorisedException;

    String getAuthoriseUriFromRequest(HttpServletRequest request);

}