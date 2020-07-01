package uk.gov.ch.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.ch.oauth.tokens.OAuthToken;
import uk.gov.ch.oauth.tokens.UserProfileResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.store.Store;

@ExtendWith(MockitoExtension.class)
public class Oauth2Test {

    private static final String CODE = "code";
    private static final String NONCE = "noncevalue";
    private static final String STATE = "state";
    private static OAuthToken oauthToken;
    private static UserProfileResponse userProfile;
    private final SignInInfo signInInfo = new SignInInfo();
    public static MockWebServer mockServer;
    static final String ORIGINAL_REQUEST_URL = "https://www.example.com?original";
    static final StringBuffer REQUEST_URL_STRING_BUFFER =
            new StringBuffer("https://www.example.com");

    @Mock
    public IIdentityProvider identityProvider;
    @Mock
    private Store store;
    @Mock
    private Session session;
    @Mock
    private Cookie cookie;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private HttpServletRequest request;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OAuth2StateHandler oAuth2StateHandler;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private Logger mockLog;

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

        userProfile = new UserProfileResponse();
        userProfile.setEmail("demo@test.gov.uk");
        userProfile.setId("1");
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
        verify(mockLog).error("OAuth server has returned a status of [403 FORBIDDEN] when attempting to request a User Profile");

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
    @DisplayName("Test that the expected OAuthToken object is returned when server returns a successful response")
    public void testRequestOAuthTokenWithASuccessfullResponse()
            throws JsonProcessingException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();

        HttpUrl url = mockServer.url("/oauth2/token");
        when(identityProvider.getTokenUrl()).thenReturn(url.toString());

        when(identityProvider.getPostRequestBody(anyString())).thenReturn("");

        mockServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.ACCEPTED.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody(mapper.writeValueAsString(oauthToken)));

        OAuthToken oauthTokenResponse = oauth2.requestOAuthToken(anyString());

        assertNotNull(oauthTokenResponse);
        assertEquals("Bearer", oauthTokenResponse.getTokenType());
        assertEquals(3600, oauthTokenResponse.getExpiresIn());
        assertEquals("refreshToken", oauthTokenResponse.getRefreshToken());
        assertEquals("token", oauthTokenResponse.getToken());

        RecordedRequest recordedRequest = mockServer.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/oauth2/token", recordedRequest.getPath());
    }
    
    @Test
    @DisplayName("Test that the expected OAuthToken object is returned when server returns a successful response")
    public void testRequestOAuthTokenWithAForbiddenResponse()
            throws JsonProcessingException, InterruptedException {
        HttpUrl url = mockServer.url("/oauth2/token");
        when(identityProvider.getTokenUrl()).thenReturn(url.toString());

        when(identityProvider.getPostRequestBody(anyString())).thenReturn("");

        mockServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.FORBIDDEN.value()));

        OAuthToken oauthTokenResponse = oauth2.requestOAuthToken(anyString());

        assertNotNull(oauthTokenResponse);
        assertNull(oauthTokenResponse.getTokenType());
        verify(mockLog).error(
                "OAuth server has returned a status of [403 FORBIDDEN] when attempting to request an Access Token");

        RecordedRequest recordedRequest = mockServer.takeRequest();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/oauth2/token", recordedRequest.getPath());
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

    @Test
    @DisplayName("Test validate() returns true when correct User Profile is returned")
    public void testValidateReturnsTrue() throws JsonProcessingException, InterruptedException {
        when(oAuth2StateHandler.oauth2DecodeState(anyString()).toJSONObject().getAsString("nonce"))
                .thenReturn(NONCE);

        Map<String, Object> data = new HashMap<>();
        data.put(SessionKeys.NONCE.getKey(), NONCE);
        when(sessionFactory.getSessionDataFromContext()).thenReturn(data);

        doReturn(oauthToken).when(oauth2).requestOAuthToken(anyString());
        when(sessionFactory.regenerateSession()).thenReturn(session);
        when(sessionFactory.buildSessionCookie(session)).thenReturn(cookie);
        doReturn(userProfile).when(oauth2).requestUserProfile(oauthToken);
        when(sessionFactory.getSessionFromContext()).thenReturn(session);

        assertTrue(oauth2.validate(STATE, CODE, httpServletResponse));
    }

    @Test
    @DisplayName("Test validate() returns false when fetchUserProfile() returns null due to an empty userProfile being returned")
    public void testValidateReturnsFalse() throws JsonProcessingException, InterruptedException {
        when(oAuth2StateHandler.oauth2DecodeState(anyString()).toJSONObject().getAsString("nonce"))
                .thenReturn(NONCE);

        Map<String, Object> data = new HashMap<>();
        data.put(SessionKeys.NONCE.getKey(), NONCE);
        when(sessionFactory.getSessionDataFromContext()).thenReturn(data);

        doReturn(oauthToken).when(oauth2).requestOAuthToken(CODE);
        when(sessionFactory.regenerateSession()).thenReturn(session);
        when(sessionFactory.buildSessionCookie(session)).thenReturn(cookie);

        UserProfileResponse userProfile = new UserProfileResponse();
        doReturn(userProfile).when(oauth2).requestUserProfile(oauthToken);

        assertFalse(oauth2.validate(STATE, CODE, httpServletResponse));
    }

    @Test
    @DisplayName("Test validate() when getSessionNonce() throws a NullPointerException")
    public void testGetSessionNonceThrowsClassCastException() {
        when(oAuth2StateHandler.oauth2DecodeState(anyString()).toJSONObject().getAsString("nonce"))
                .thenReturn(NONCE);

        final Map<String, Object> sessionData = Mockito.spy(new HashMap<>());
        when(sessionFactory.getSessionDataFromContext()).thenReturn(sessionData);
        when(sessionData.remove(SessionKeys.NONCE.getKey())).thenReturn(1);// Returns an int to
                                                                           // trigger a
                                                                           // ClassCastException.

        assertFalse(oauth2.validate(STATE, CODE, httpServletResponse));
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
