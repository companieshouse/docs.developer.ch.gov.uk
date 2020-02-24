package uk.gov.ch.oauth;

public interface IIdentityProvider {

    byte[] getRequestKey();

    String getAuthorisationUrl(final String state);
}
