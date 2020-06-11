package uk.gov.ch.oauth.identity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class IdentityProviderTests {


    private IdentityProvider identityProvider;


    private EnvironmentReaderImpl reader = new EnvironmentReaderImpl();
    private final String CODE = "code";
    private final String STATE = "state";
    private final String SCOPE = "scope";

    @BeforeEach
    public void init() {
        identityProvider = new IdentityProvider(reader);
    }


    @Test
    @DisplayName("Test that getAuthorisationUrl(final String State) returns the expected result")
    public void testThatGetAuthorisationUrlWithStateParameterReturnsTheExpectedUrl() {
        String expectedAuthUrl = "/oauth2/authorise?client_id=test.apps.ch.gov.test&redirect_uri=/oauth2/user/callback&response_type=code&state=state";
        String authUrl = identityProvider.getAuthorisationUrl(STATE);
        assertEquals(expectedAuthUrl, authUrl);
    }

    @Test
    @DisplayName("Test that getAuthorisationUrl(final String State, final String scope) returns the expected result")
    public void testThatGetAuthorisationWithScopeAndStateParameterReturnsTheExpectedURL() {
        String expectedUrl = "/oauth2/authorise?client_id=test.apps.ch.gov.test&redirect_uri=/oauth2/user/callback&response_type=code&state=state&scope=scope";
        String authUrl = identityProvider.getAuthorisationUrl(STATE, SCOPE);
        assertEquals(expectedUrl, authUrl);
    }

    @Test
    public void testThatGetPostRequestBodyReturnsTheExpectedResult() {
        String expectedRequestBody = "code=code&client_id=test.apps.ch.gov.test&client_secret=CLIwYzRkNzIwOGQ1OGQ0OWIzMzViYjJjOTEyYTc2&redirect_uri=/oauth2/user/callback&grant_type=authorization_code";
        String actualRequestBody = identityProvider.getPostRequestBody(CODE);
        assertEquals(expectedRequestBody, actualRequestBody);
    }

}
