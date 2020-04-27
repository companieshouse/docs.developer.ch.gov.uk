package uk.gov.ch.oauth;

import javax.servlet.http.HttpServletResponse;
import uk.gov.companieshouse.session.Session;

public interface IOauth {

    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

    String encodeSignInState(final String returnUri,
            final Session sessions,
            final String attributeName);

    boolean isValid(String state, String code, HttpServletResponse httpServletResponse);
}
