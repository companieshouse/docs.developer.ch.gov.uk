package uk.gov.ch.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@ExtendWith(MockitoExtension.class)
class NonceGeneratorTest {

    private static final String NONCE = "NONCE";
    @Mock
    Session mockSession;
    @Mock
    Map<String, Object> mockData;

    @Spy //Spy used to allow us to remove randomness from Nonce generation
            NonceGenerator nonceGenerator;

    @Test
    @DisplayName("Expected path")
    void setNonceForSession() {
        doReturn(NONCE).when(nonceGenerator).generateNonce(); // Do return used rather than when to
        // prevent code coverage marking this as done.
        when(mockSession.getData()).thenReturn(mockData);

        nonceGenerator.setNonceForSession(mockSession);

        verify(mockSession).getData();
        verify(mockData).put(SessionKeys.NONCE.getKey(), NONCE);
        verify(nonceGenerator).setNonceForSession(mockSession);
        verify(nonceGenerator).generateNonce();
        verifyNoMoreInteractions(nonceGenerator, mockSession, mockData);
    }

    @Test
    @DisplayName("Session is null does not cause an error")
    void doesNotErrorWhenNonceIsNull() {
        doReturn(NONCE).when(nonceGenerator).generateNonce(); // Do return used rather than when to
        // prevent code coverage marking this as done.
        String retNonce = nonceGenerator.setNonceForSession(null);
        assertEquals(NONCE, retNonce);
    }

    @Test
    @DisplayName("Generate Nonce creates unique values, brute force test")
    void generateNonceGeneratesUniqueValues() {
        List<String> nonces = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String newNonce = nonceGenerator.generateNonce();
            System.out.println(newNonce);
            assertFalse(nonces.contains(newNonce));
            nonces.add(newNonce);
        }
    }
}