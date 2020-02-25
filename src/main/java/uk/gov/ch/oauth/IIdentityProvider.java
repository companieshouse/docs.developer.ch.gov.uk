package uk.gov.ch.oauth;

public interface IIdentityProvider {

    String getClientSecret();

    /**
     * Get the key from based on current request
     */
    byte[] getRequestKey();

    String getRedirectUri();

    String getClientId();

    String getAuthorisationUrl(final String state);

    String getAuthorisationUrl(final String originalRequestUri, final String scope);
}
