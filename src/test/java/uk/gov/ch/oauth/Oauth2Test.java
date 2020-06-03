package uk.gov.ch.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.tokens.OAuthToken;
import uk.gov.ch.oauth.tokens.UserProfileResponse;

@ExtendWith(MockitoExtension.class)
public class Oauth2Test {

    public static MockWebServer mockServer;

    @Mock
    public IIdentityProvider identityProvider;
    @Mock
    public HttpServletResponse httpServletResponse;
    @Mock
    public ClientResponse response;


    @InjectMocks
    public Oauth2 oauth2;

    @BeforeAll
    static void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("Test that an empty userProfileResponse object is returned when OAuth server returns a 403 response")
    public void testRequestUserProfileWithAForbiddenResponse() throws Exception {

        OAuthToken oauthToken = new OAuthToken();

        oauthToken.setExpiresIn(3600);
        oauthToken.setRefreshToken("refreshToken");
        oauthToken.setToken("token");
        oauthToken.setTokenType("Bearer");

        HttpUrl url = mockServer.url("/user/profile");

        when(identityProvider.getProfileUrl()).thenReturn(url.toString());

        mockServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.FORBIDDEN.value()));

        UserProfileResponse userProfileResponse = oauth2.requestUserProfile(oauthToken);

        assertNotNull(userProfileResponse);
        assertNull(userProfileResponse.getId());

        RecordedRequest recordedRequest = mockServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/user/profile", recordedRequest.getPath());
    }

    @Test
    @DisplayName("Test that the expected userProfileResponse object is returned when OAuth server returns a 202 response")
    public void testRequestUserProfileWithASuccessfulResponse() throws Exception {

        OAuthToken oauthToken = new OAuthToken();

        oauthToken.setExpiresIn(3600);
        oauthToken.setRefreshToken("refreshToken");
        oauthToken.setToken("token");
        oauthToken.setTokenType("Bearer");

        HttpUrl url = mockServer.url("/user/profile");

        when(identityProvider.getProfileUrl()).thenReturn(url.toString());

        mockServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.ACCEPTED.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(
                        "{\"surname\":null,\"forename\":null,\"locale\":\"GB_en\",\"email\":\"demo@test.gov.uk\",\"id\":\"12345\"}"));

        UserProfileResponse userProfileResponse = oauth2.requestUserProfile(oauthToken);

        assertNotNull(userProfileResponse);
        assertEquals("demo@test.gov.uk", userProfileResponse.getEmail());

        RecordedRequest recordedRequest = mockServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/user/profile", recordedRequest.getPath());
    }


}
