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


@ExtendWith(MockitoExtension.class)
class OAuthCoordinatorTest {

    @Spy
    @InjectMocks
    OAuthCoordinator oAuthCoordinator;

    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private IIdentityProvider mockIdentityProvider;
    @Mock
    private IOauth mockOAuth;

    @Nested
    class CallbackTests {

        @Test
        @DisplayName("Throws Exception if callback params contains error.")
        void getPostCallbackRedirectURLWhenErrorResponse() throws IOException {
            Map<String, String> params = new HashMap<>();
            params.put("error", "Error Value");

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "Error Value");
        }

        @Test
        @DisplayName("Throws Exception if callback do not contain State.")
        void getPostCallbackRedirectURLWhenMissingValuesState()
                throws UnauthorisedException, IOException {
            Map<String, String> params = new HashMap<>();
            params.put("code", "Code");

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access token");
            verify(oAuthCoordinator, never())
                    .validateResponse(anyString(), anyString(), any(HttpServletResponse.class));
        }

        @Test
        @DisplayName("Throws Exception if callback do not contain Code.")
        void getPostCallbackRedirectURLWhenMissingValuesCode()
                throws UnauthorisedException, IOException {
            Map<String, String> params = new HashMap<>();
            params.put("state", "State");

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockResponse)
                    .sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access token");
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
            params.put("state", "State");
            params.put("code", "Code");

            assertThrows(UnauthorisedException.class,
                    () -> oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params));

            verify(mockOAuth).validate("State", "Code", mockResponse);
        }

        @Test
        @DisplayName("Get Post Back Returns identity provider results")
        void getPostCallbackRedirectReturnsRedirectURLWhenValuesAreCorrect()
                throws UnauthorisedException {
            doReturn("Redirect URL").when(mockIdentityProvider).getRedirectUriPage();
            doReturn(mockIdentityProvider).when(oAuthCoordinator).getIdentityProvider();
            doReturn(mockOAuth).when(oAuthCoordinator).getOAuth();
            when(mockOAuth.validate(anyString(), anyString(), any(HttpServletResponse.class)))
                    .thenReturn(true);

            Map<String, String> params = new HashMap<>();
            params.put("state", "State");
            params.put("code", "Code");

            String ret = oAuthCoordinator.getPostCallbackRedirectURL(mockResponse, params);

            assertEquals("Redirect URL", ret);
            verify(mockOAuth).validate("State", "Code", mockResponse);
            verify(mockIdentityProvider).getRedirectUriPage();
        }
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