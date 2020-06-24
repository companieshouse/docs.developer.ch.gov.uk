package uk.gov.ch.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.ch.oauth.tokens.OAuthToken;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.store.Store;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Oauth2Test {

    public static MockWebServer mockServer;
    private static OAuthToken oauthToken;
    private final SignInInfo signInInfo = new SignInInfo();
    static final String ORIGINAL_REQUEST_URL = "https://www.example.com?original";
    static final StringBuffer REQUEST_URL_STRING_BUFFER = new StringBuffer(
            "https://www.example.com"); //TODO decide if we should be expecting StringBuffer here or not
    public static final String AUTHORISE_URI = "https://example.com/authorise";
    static final String STATE = "state";

    @Mock
    public IIdentityProvider identityProvider;
    @Mock
    private Store store;
    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private HttpServletRequest request;

    @Spy
    @InjectMocks
    public Oauth2 oauth2;

    @BeforeAll
    public static void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        oauthToken = new OAuthToken();

        oauthToken.setExpiresIn(3600);
        oauthToken.setRefreshToken("refreshToken");
        oauthToken.setToken("token");
        oauthToken.setTokenType("Bearer");
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void setUpBeforeEach() {
        lenient().when(sessionFactory.getDefaultStore()).thenReturn(store);
    }

    @Test
    @DisplayName("Test that an empty userProfileResponse object is returned when OAuth server returns a 403 response")
    public void testRequestUserProfileWithAForbiddenResponse() throws Exception {
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
        ObjectMapper mapper = new ObjectMapper();

        UserProfileResponse userProfile = new UserProfileResponse();
        userProfile.setEmail("demo@test.gov.uk");

        HttpUrl url = mockServer.url("/user/profile");
        when(identityProvider.getProfileUrl()).thenReturn(url.toString());

        mockServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.ACCEPTED.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(userProfile)));

        UserProfileResponse userProfileResponse = oauth2.requestUserProfile(oauthToken);

        assertNotNull(userProfileResponse);
        assertEquals("demo@test.gov.uk", userProfileResponse.getEmail());

        RecordedRequest recordedRequest = mockServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/user/profile", recordedRequest.getPath());
    }

    @Test
    @DisplayName("Test that a valid signed in user's session state is correctly altered")
    public void testInvalidateSessionWhenSignedIn() {
        final String zxsValue = "0000000001z";
        signInInfo.setSignedIn(true);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        Map<String, Object> data = setUserSessionData(zxsValue);
        when(session.getData()).thenReturn(data);
        oauth2.invalidateSession(session);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));
        verify(store, only()).delete(zxsValue);

    }

    @Test
    @DisplayName("Test that a not signed in user is unable to sign out")
    public void testNotSignedOutUserIsUnableSignOut() {
        final String zxsValue = "0000000001z";
        Map<String, Object> data = setUserSessionData(zxsValue);
        signInInfo.setSignedIn(false);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        when(session.getData()).thenReturn(data);
        oauth2.invalidateSession(session);

        verifyNoMoreInteractions(session);
    }

    @Test
    @DisplayName("Test signout if the user has an invalid ZXSKey")
    public void testSignoutWhenUserHasAnInvalidKey() {
        final String zxsValue = null;
        signInInfo.setSignedIn(true);
        when(session.getSignInInfo()).thenReturn(signInInfo);
        Map<String, Object> data = setUserSessionData(zxsValue);
        when(session.getData()).thenReturn(data);
        oauth2.invalidateSession(session);

        assertFalse(data.containsKey(SessionKeys.SIGN_IN_INFO.getKey()));
        verifyNoMoreInteractions(session);
    }

    @Test
    @DisplayName("Test that prepareState returns a valid State String")
    public void testPrepareState() {
        doReturn(STATE).when(oauth2).encodeSignInState(ORIGINAL_REQUEST_URL, session, "content");
        when(sessionFactory.getSessionFromContext()).thenReturn(session);
        when(request.getRequestURL()).thenReturn(REQUEST_URL_STRING_BUFFER);
        when(request.getQueryString()).thenReturn("original");
        String state = oauth2.prepareState(request);
        assertEquals(STATE, state);
    }


    private Map<String, Object> setUserSessionData(String zxsValue) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> signInData = new HashMap<>();
        signInData.put(SessionKeys.SIGNED_IN.getKey(), 1);
        data.put(SessionKeys.SIGN_IN_INFO.getKey(), signInData);
        data.put(".zxs_key", zxsValue);
        return data;
    }

}
