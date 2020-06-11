package uk.gov.ch.oauth.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@ExtendWith(MockitoExtension.class)
class SessionSignInModifierTest {

    @Spy
    SessionSignInModifier sessionSignInModifier;
    @Mock
    private Session mockSession;
    @Mock
    private OAuthToken mockOAuthToken;
    @Mock
    private UserProfileResponse mockUserResponse;
    @Mock
    private Map<String, Object> sessionMap;
    @Mock
    private Map<String, Object> mockOAuthTokenMap;
    @Mock
    private Map<String, Object> mockUserResponseMap;

    @Test
    void alterSessionData_withObjects_delegatesWithCorrectMaps() {
        when(mockSession.getData()).thenReturn(sessionMap);
        when(mockOAuthToken.getAccessTokenAsMap()).thenReturn(mockOAuthTokenMap);
        when(mockUserResponse.getUserProfileAsMap()).thenReturn(mockUserResponseMap);

        //Guys want a second opinion on whether this is good or bad practice
        doAnswer(assertNewMapIsCorrect()).when(sessionSignInModifier)
                .alterSessionData(argThat(matchesSessionMap()), anyMap());

        sessionSignInModifier.alterSessionData(mockSession, mockOAuthToken, mockUserResponse);

        verify(mockOAuthToken).getAccessTokenAsMap();
        verify(mockUserResponse).getUserProfileAsMap();
        verify(mockSession).getData();
        verify(sessionSignInModifier)
                .alterSessionData(mockSession, mockOAuthToken, mockUserResponse);
        verifyNoMoreInteractions(mockSession, mockOAuthToken, mockUserResponse,
                sessionSignInModifier);
    }

    @Test
    @DisplayName("Test Alter Session Data Puts values in places")
    void testAlterSessionData_UpdatesWhenThereIsNoExistingSignIn() {
        Map<String, Object> currentSessionMap = new HashMap<>();
        Map<String, Object> newSignInMap = new HashMap<>();
        newSignInMap.put(SessionKeys.ACCESS_TOKEN.getKey(), mockOAuthTokenMap);
        newSignInMap.put(SessionKeys.USER_PROFILE.getKey(), mockUserResponseMap);
        newSignInMap.put(SessionKeys.SIGNED_IN.getKey(), 1);

        assertNull(currentSessionMap.get(SessionKeys.SIGN_IN_INFO.getKey()));

        sessionSignInModifier.alterSessionData(currentSessionMap, newSignInMap);

        Map<String, Object> updatedSignInMap = (Map<String, Object>) currentSessionMap
                .get(SessionKeys.SIGN_IN_INFO.getKey());

        assertEquals(newSignInMap, updatedSignInMap);
        assertEquals(mockOAuthTokenMap, updatedSignInMap.get(SessionKeys.ACCESS_TOKEN.getKey()));
        assertEquals(mockUserResponseMap, updatedSignInMap.get(SessionKeys.USER_PROFILE.getKey()));
        assertEquals(1, updatedSignInMap.get(SessionKeys.SIGNED_IN.getKey()));
    }

    private ArgumentMatcher<Map<String, Object>> matchesSessionMap() {
        return argument -> argument.equals(sessionMap);
    }

    private Answer assertNewMapIsCorrect() {
        return invocation -> {
            Map<String, Object> generatedMap = invocation.getArgument(1);
            assertEquals(1, generatedMap.get(SessionKeys.SIGNED_IN.getKey()));
            assertEquals(mockOAuthTokenMap,
                    generatedMap.get(SessionKeys.ACCESS_TOKEN.getKey()));
            assertEquals(mockUserResponseMap,
                    generatedMap.get(SessionKeys.USER_PROFILE.getKey()));
            return null;
        };
    }
}