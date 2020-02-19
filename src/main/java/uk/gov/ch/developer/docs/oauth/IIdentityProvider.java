package uk.gov.ch.developer.docs.oauth;

public interface IIdentityProvider {

    byte[] getRequestKey();

    String getAuthorisationUrl(final String state);
}
