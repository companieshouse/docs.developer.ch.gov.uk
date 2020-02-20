package uk.gov.ch.developer.docs.oauth;

import java.util.Base64;
import java.util.Base64.Decoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class IdentityProvider implements IIdentityProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");
    private static final String OAUTH_COMPANY_SCOPE_PREFIX =
            "https://api.companieshouse.gov.uk/company/";
    private final static Decoder decoder = Base64.getDecoder();
    //final private String base64Key;
    final private byte[] requestKey;
    final private String authorizationUri;
    final private String clientId;
    final private String redirectUri;
    final private String clientSecret;

    @Autowired
    public IdentityProvider(final EnvironmentReader reader) {
        // this.base64Key = reader.getMandatoryString("OAUTH2_REQUEST_KEY");
        requestKey = decoder.decode(reader.getMandatoryString("DEVELOPER_OAUTH2_REQUEST_KEY"));
        this.authorizationUri = reader.getMandatoryString("OAUTH2_AUTH_URI");
        this.clientId = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_ID");
        redirectUri = reader.getMandatoryString("OAUTH2_REDIRECT_URI");
        clientSecret = reader.getMandatoryString("CHS_DEVELOPER_CLIENT_SECRET");
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public byte[] getRequestKey() {
        return requestKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    private String getAuthorisationUrl(final String scope, final String state) {
        StringBuilder sb = new StringBuilder(getAuthorisationUrl(state));
        if (scope != null) {
            sb.append("&scope=");
            sb.append(scope);
        }
        return sb.toString();
    }

    public String getAuthorisationUrl(final String state) {
        String sb = getAuthorizationUri()
                + "?"
                + "client_id="
                + getClientId()
                + "&redirect_uri="
                + getRedirectUri()
                + "&response_type=code"
                + "&state="
                + state;
        return sb;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

}
