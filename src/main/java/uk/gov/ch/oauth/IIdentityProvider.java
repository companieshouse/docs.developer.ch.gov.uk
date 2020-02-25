package uk.gov.ch.oauth;

/**
 * Fields are based on the following yaml for old developer hub
 * <pre>
 *         redirect_uri  : '<DEVELOPER_URL>/developer/oauth2/user/callback'
 *         client_id     : '<CHS_DEVELOPER_CLIENT_ID>'
 *         client_secret : '<CHS_DEVELOPER_CLIENT_SECRET>'
 *         authorize_url : '<ACCOUNT_URL>/oauth2/authorise'
 *         token_url     : '<ACCOUNT_LOCAL_URL>/oauth2/token'
 *         profile_url   : '<ACCOUNT_LOCAL_URL>/user/profile'
 *         home_url      : '<DEVELOPER_URL>'
 *  </pre>
 */
public interface IIdentityProvider {

    String getClientSecret();

    /**
     * Get the key from based on current request
     */
    byte[] getRequestKey();

    String getRedirectUri();

    String getClientId();

    /**
     * Authorisation URL needs to include {@literal state} information
     *
     * @return URL with embedded state information
     * @see #getAuthorisationUrl(String, String)
     */
    String getAuthorisationUrl(final String state);

    /**
     * Authorisation URL generated with {@literal state} and {@literal scope}
     *
     * @see #getAuthorisationUrl(String)
     */
    String getAuthorisationUrl(final String originalRequestUri, final String scope);

    String getTokenUrl();

    String getProfileUrl();

    String getHomeUrl();
}
