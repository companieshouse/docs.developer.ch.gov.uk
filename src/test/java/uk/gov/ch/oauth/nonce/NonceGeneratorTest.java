package uk.gov.ch.oauth.nonce;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.oauth.session.SessionFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;

@ExtendWith(MockitoExtension.class)
class NonceGeneratorTest {

    private static final String NONCE = "NONCE";
    @Mock
    SessionFactory mockUtils;
    @Mock
    Session mockSession;
    @Mock
    Map<String, Object> mockData;

    @InjectMocks
    @Spy//Spy used to allow us to remove randomness from Nonce generation
            NonceGenerator nonceGenerator;

    @Test
    @DisplayName("Expected path")
    void setNonceForSession() {
        doReturn(NONCE).when(nonceGenerator).generateNonce(); // Do return used rather than when to
        // prevent code coverage marking this as done.
        when(mockSession.getData()).thenReturn(mockData);

        nonceGenerator.addNonceToSession(mockSession);

        verify(mockSession).getData();
        verify(mockData).put(SessionKeys.NONCE.getKey(), NONCE);
        verify(nonceGenerator).addNonceToSession(mockSession);
        verify(nonceGenerator).generateNonce();
        verifyNoMoreInteractions(nonceGenerator, mockSession, mockData);
    }

    @Test
    @DisplayName("Session is null does not cause an error")
    void doesNotErrorWhenNonceIsNull() {
        doReturn(mockSession).when(mockUtils).createSession();
        doReturn(NONCE).when(nonceGenerator).generateNonce(); // Do return used rather than when to
        // prevent code coverage marking this as done.
        String retNonce = nonceGenerator.addNonceToSession(null);
        verify(mockSession).getData();
        assertEquals(NONCE, retNonce);
    }

    @Test
    @DisplayName("Generate Nonce creates unique values, brute force test")
    void generateNonceGeneratesUniqueValues() {
        Set<String> nonces = new TreeSet<>();
        final int iterations = 100;
        for (int i = 0; i < iterations; i++) {
            String newNonce = nonceGenerator.generateNonce();
            nonces.add(newNonce);
        }
        assertEquals(iterations, nonces.size());
    }
}