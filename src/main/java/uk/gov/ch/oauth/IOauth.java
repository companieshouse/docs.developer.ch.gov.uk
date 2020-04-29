package uk.gov.ch.oauth;

import uk.gov.companieshouse.session.Session;

import javax.servlet.http.HttpServletResponse;

public interface IOauth {

    String oauth2EncodeState(final String returnUri,
            final String nonce,
            final String attributeName);

    String encodeSignInState(final String returnUri,
            final Session sessions,
            final String attributeName);

    boolean validate(String state, String code, HttpServletResponse httpServletResponse);
}
