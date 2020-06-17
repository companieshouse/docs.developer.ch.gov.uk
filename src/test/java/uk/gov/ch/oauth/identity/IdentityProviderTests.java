package uk.gov.ch.oauth.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;


@ExtendWith(MockitoExtension.class)
public class IdentityProviderTests {


    private IdentityProvider provider;

    private EnvironmentReaderImpl reader;
    private static final String CODE = "code";
    private static final String STATE = "state";
    private static final String SCOPE = "scope";
    private static final String AUTHORISATIONURI = "/oauth2/authorise";
    private static final String CLIENTID = "test.apps.ch.gov.test";
    private static final String REDIRECTURI = "/oauth2/user/callback";
    private static final String CLIENTSECRET = "CLIwYzRkNzIwOGQ1OGQ0OWIzMzViYjJjOTEyYTc2";
    private static final String GRANTTYPE = "authorization_code";

    @BeforeEach
    public void init() {
        reader = new EnvironmentReaderImpl();
        provider = new IdentityProvider(reader);
    }


    @Test
    @DisplayName("Test that getAuthorisationUrl(final String State) returns the expected result")
    public void testThatGetAuthorisationUrlWithStateParameterReturnsTheExpectedUrl() {
        String expectedAuthUrl =
                AUTHORISATIONURI + "?client_id=" + CLIENTID + "&redirect_uri=" + REDIRECTURI
                        + "&response_type=code&state=" + STATE;
        String authUrl = provider.getAuthorisationUrl(STATE);
        assertEquals(expectedAuthUrl, authUrl);
    }

    @Test
    @DisplayName("Test that getAuthorisationUrl(final String State, final String scope) returns the expected result")
    public void testThatGetAuthorisationWithScopeAndStateParameterReturnsTheExpectedURL() {
        String expectedUrl =
                AUTHORISATIONURI + "?client_id=" + CLIENTID + "&redirect_uri=" + REDIRECTURI
                        + "&response_type=" + CODE + "&state=" + STATE + "&scope=" + SCOPE;
        String authUrl = provider.getAuthorisationUrl(STATE, SCOPE);
        assertEquals(expectedUrl, authUrl);
    }

    @Test
    public void testThatGetPostRequestBodyReturnsTheExpectedResult() {
        String expectedRequestBody =
                "code=" + CODE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
                        + "&redirect_uri=" + REDIRECTURI + "&grant_type=" + GRANTTYPE;
        String actualRequestBody = provider.getPostRequestBody(CODE);
        assertEquals(expectedRequestBody, actualRequestBody);
    }
}