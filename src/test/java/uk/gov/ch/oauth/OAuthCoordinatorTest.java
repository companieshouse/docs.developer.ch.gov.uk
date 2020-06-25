package uk.gov.ch.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;
import uk.gov.ch.oauth.identity.IIdentityProvider;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.session.Session;


@ExtendWith(MockitoExtension.class)
class OAuthCoordinatorTest {

    public static final String CODE_KEY = "code";
    public static final String CODE_VALUE = "Code";
    public static final String STATE_KEY = "state";
    public static final String STATE_VALUE = "State";
    public static final String ERROR_KEY = "error";
    public static final String ERROR_VALUE = "Error Value";
    public static final String REDIRECT_URL = "Redirect URL";
    public static final String INVALID_ACCESS_TOKEN_MSG = "Invalid access token";

    @Spy
    @InjectMocks
    OAuthCoordinator oAuthCoordinator;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private IIdentityProvider mockIdentityProvider;
    @Mock
    private IOauth mockOAuth;
    @Mock
    private HttpServletRequest request;

    private Session session;


    @Nested
    class CallbackTests {

        @Test
        @DisplayName("Throws Exception if callback params contains error.")
        void getPostCallbackRedirectURLWhenErrorResponse() throws IOException {
            Map<String, String> params = new HashMap<>();
            params.put(ERROR_KEY, ERROR_VALUE);

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN, ERROR_VALUE);
        }

        @Test
        @DisplayName("Throws Exception if callback do not contain State.")
        void getPostCallbackRedirectURLWhenMissingValuesState()
                throws UnauthorisedException, IOException {
            Map<String, String> params = new HashMap<>();
            params.put(CODE_KEY, CODE_VALUE);

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, INVALID_ACCESS_TOKEN_MSG);
            verify(oAuthCoordinator, never())
                    .validateResponse(anyString(), anyString(), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Throws Exception if callback do not contain Code.")
        void getPostCallbackRedirectURLWhenMissingValuesCode()
                throws UnauthorisedException, IOException {
            Map<String, String> params = new HashMap<>();
            params.put(STATE_KEY, STATE_VALUE);

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, INVALID_ACCESS_TOKEN_MSG);
            verify(oAuthCoordinator, never())
                    .validateResponse(anyString(), anyString(), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Get Post Back Returns identity provider results")
        void getPostCallbackRedirectThrowsWhenValidationFails() {
            doReturn(mockOAuth).when(oAuthCoordinator).getOAuth();
            when(mockOAuth.validate(anyString(), anyString(), any(HttpServletResponse.class)))
                    .thenReturn(false);

            Map<String, String> params = new HashMap<>();
            params.put(STATE_KEY, STATE_VALUE);
            params.put(CODE_KEY, CODE_VALUE);

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockOAuth).validate(STATE_VALUE, CODE_VALUE, mockResponse);
        }

        @Test
        @DisplayName("Get Post Back Returns identity provider results")
        void getPostCallbackRedirectReturnsRedirectURLWhenValuesAreCorrect()
                throws UnauthorisedException {
            doReturn(REDIRECT_URL).when(mockIdentityProvider).getRedirectUriPage();
            doReturn(mockIdentityProvider).when(oAuthCoordinator).getIdentityProvider();
            doReturn(mockOAuth).when(oAuthCoordinator).getOAuth();
            when(mockOAuth.validate(anyString(), anyString(), any(HttpServletResponse.class)))
                    .thenReturn(true);

            Map<String, String> params = new HashMap<>();
            params.put(STATE_KEY, STATE_VALUE);
            params.put(CODE_KEY, CODE_VALUE);

            String ret = oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params);

            assertEquals(REDIRECT_URL, ret);
            verify(mockOAuth).validate(STATE_VALUE, CODE_VALUE, mockResponse);
            verify(mockIdentityProvider).getRedirectUriPage();
        }
    }


    @DisplayName("getAuthoriseUriFromRequestTest")
    void getAuthoriseUriFromRequestTest() {
        when(oAuthCoordinator.getOAuth()).thenReturn(mockOAuth);
        when(mockOAuth.prepareState(request)).thenReturn(STATE_KEY);
        when(oAuthCoordinator.getIdentityProvider()).thenReturn(mockIdentityProvider);

        oAuthCoordinator.getAuthoriseUriFromRequest(request);

        verify(mockIdentityProvider).getAuthorisationUrl(STATE_VALUE);
    }

    @Test
    @DisplayName("Test that getSignoutUri returns a value from IdentityProviders getRedirectUri()")
    void testGetSignOutUri() {
        when(oAuthCoordinator.getIdentityProvider()).thenReturn(mockIdentityProvider);
        when(mockIdentityProvider.getRedirectUriPage()).thenReturn("/home");
        String redirectPage = oAuthCoordinator.getSignoutUri();
        assertEquals("/home", redirectPage);
    }

    @Test
    @DisplayName("Test that OAuthCoordinators invalidateSession calls Oauth2's invalidate session")
    void testOAuthCoordinatorInvalidateSession() {
        when(oAuthCoordinator.getOAuth()).thenReturn(mockOAuth);
        oAuthCoordinator.invalidateSession(session);
        verify(mockOAuth).invalidateSession(session);

    }

    @Nested
    class lazyGetterTests {

        @Test
        @DisplayName("Creates an IOAuth object if one doesn't exist and returns the same one if it does.")
        void testGetOAuth() {
            IOauth auth = oAuthCoordinator.getOAuth();
            assertNotNull(auth);
            assertEquals(auth, oAuthCoordinator.getOAuth());
        }

        @Test
        @DisplayName("Creates an IIdentityProvider object if one doesn't exist and returns the same one if it does.")
        void testGetIdentityProvider() {
            IIdentityProvider identityProvider = oAuthCoordinator.getIdentityProvider();
            assertNotNull(identityProvider);
            assertEquals(identityProvider, oAuthCoordinator.getIdentityProvider());
        }

        @Test
        @DisplayName("Creates an Environment Reader object if one doesn't exist and returns the same one if it does.")
        void testGetEnvironmentReader() {
            EnvironmentReader environmentReader = oAuthCoordinator.getEnvironmentReader();
            assertNotNull(environmentReader);
            assertEquals(environmentReader, oAuthCoordinator.getEnvironmentReader());
        }

        @Test
        @DisplayName("Creates an Session Factory object if one doesn't exist and returns the same one if it does.")
        void testGetSessionFactory() {
            SessionFactory sessionFactory = oAuthCoordinator.getSessionFactory();
            assertNotNull(sessionFactory);
            assertEquals(sessionFactory, oAuthCoordinator.getSessionFactory());
        }
    }
}