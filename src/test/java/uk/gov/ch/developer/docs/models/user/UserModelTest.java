package uk.gov.ch.developer.docs.models.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@ExtendWith(MockitoExtension.class)
class UserModelTest {

    private final String email = "email";
    private final String id = "id";
    @Mock
    SignInInfo loggedInInfo;
    @Mock
    SignInInfo loggedOutInfo;
    @Mock
    Session mockSession;
    @Mock
    UserProfile loggedInProfile;
    @Mock
    UserProfile loggedOutProfile;
    private UserModel userModel = new UserModel();

    private void invalidateUser() {
        userModel.clear();

        assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
        assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
    }

    private void logoutTest() {
        doReturn(loggedOutInfo).when(mockSession).getSignInInfo();

        userModel.populateUserDetails(mockSession);

        assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
        assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
        verify(loggedOutInfo, never()).getUserProfile();
    }

    private void loginTest() throws IllegalAccessException {
        doReturn(loggedInInfo).when(mockSession).getSignInInfo();

        userModel.populateUserDetails(mockSession);

        assertEquals(email, userModel.getEmail());
        assertEquals(id, userModel.getId());
    }

    private void loggedOutMocker() {
        when(loggedOutInfo.isSignedIn()).thenReturn(false);
    }

    private void loggedInMocker() {
        when(loggedInInfo.getUserProfile()).thenReturn(loggedInProfile);

        when(loggedInProfile.getId()).thenReturn("id");
        when(loggedInProfile.getEmail()).thenReturn(email);
        when(loggedInInfo.isSignedIn()).thenReturn(true);
    }

    @Nested
    class populateTests {

        @Test
        void badSession_cannotBeAccessed() {
            userModel.populateUserDetails(null);

            assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
            assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
        }

        @Nested
        class loggedInTests {

            @BeforeEach
            void setup() {
                loggedInMocker();
            }

            @Test
            void populatedUser_returnsExpectedValues() throws IllegalAccessException {
                loginTest();
            }

            @Test
            void populatingPopulatedUser_WithNull_ActsAsSignedOut() throws IllegalAccessException {
                loginTest();
                doReturn(null).when(mockSession).getSignInInfo();

                userModel.populateUserDetails(mockSession);

                assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
                assertThrows(IllegalAccessException.class, () -> userModel.getEmail());
            }

            @Test
            void clearedUser_ActsAsSignedOut() throws IllegalAccessException {
                loginTest();
                invalidateUser();
            }
        }

        @Nested
        class loggedOutTests {

            @BeforeEach
            void setup() {
                loggedOutMocker();
            }

            @Test
            void signedOutUser_throwsIllegalAccessExceptions() {
                logoutTest();
            }
        }

        @Nested
        class loggedOutAndInTests {

            @BeforeEach
            void setup() {
                loggedInMocker();
                loggedOutMocker();
            }

            @Test
            void loggedOutUser_LogsIn() throws IllegalAccessException {
                logoutTest();
                loginTest();
            }
        }
    }

    @Nested
    class isUserSignedInTests {

        @Test
        void isUserSignedIn_ReturnsFalse_ForNull() {
            assertFalse(UserModel.isUserSignedIn(null));
        }

        @Test
        void isUserSignedIn_ReturnsFalse_ForSignedOutUser() {

            userModel.populateUserDetails(mockSession);

            assertFalse(UserModel.isUserSignedIn(userModel));
        }

        @Test
        void isUserSignedIn_ReturnsTrue_ForSignedInUser() {
            doReturn(loggedInInfo).when(mockSession).getSignInInfo();
            when(loggedInInfo.isSignedIn()).thenReturn(true);

            userModel.populateUserDetails(mockSession);

            assertTrue(UserModel.isUserSignedIn(userModel));
        }
    }
}