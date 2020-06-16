package uk.gov.ch.oauth.tokens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.session.SessionKeys;

class OAuthTokenTest {

    public static final int EXPIRES_IN = 1;
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String TOKEN = "TOKEN";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";

    private final ObjectMapper mapper = new ObjectMapper();
    private OAuthToken token;

    @BeforeEach
    public void setup() {
        token = new OAuthToken();

        token.setExpiresIn(EXPIRES_IN);
        token.setRefreshToken(REFRESH_TOKEN);
        token.setToken(TOKEN);
        token.setTokenType(TOKEN_TYPE);
    }

    @Test
    @DisplayName("Check access token serailises with correct keys and values")
    void accessTokenSerialises() throws JsonProcessingException {
        String serialised = mapper.writeValueAsString(token);
        JsonNode tokenAsTree = mapper.readValue(serialised, JsonNode.class);

        final Iterator<Entry<String, JsonNode>> fields = tokenAsTree.fields();
        AtomicInteger fieldCount = new AtomicInteger();
        fields.forEachRemaining(entry -> {
            checkNodeValue(entry, token);
            fieldCount.getAndIncrement();
        });
        assertEquals(4, fieldCount.get());
    }

    @Test
    @DisplayName("Check AccessToken deserialises correctly.")
    void setAccessTokenDeserialises() throws JsonProcessingException {
        String input = String.format(
                "{\"access_token\":\"%s\",\"expires_in\":%d,\"refresh_token\":\"%s\",\"token_type\":\"%s\"}",
                TOKEN,
                EXPIRES_IN,
                REFRESH_TOKEN,
                TOKEN_TYPE
        );
        OAuthToken parsedToken = mapper.readValue(input, OAuthToken.class);
        assertEquals(token.getTokenType(), parsedToken.getTokenType());
        assertEquals(token.getToken(), parsedToken.getToken());
        assertEquals(token.getRefreshToken(), parsedToken.getRefreshToken());
        assertEquals(token.getExpiresIn(), parsedToken.getExpiresIn());
    }

    private void checkNodeValue(Entry<String, JsonNode> entry, OAuthToken token) {
        SessionKeys fieldKey = matchEntryToEnum(entry.getKey());
        final JsonNode value = entry.getValue();
        switch (Objects.requireNonNull(fieldKey)) {
            case ACCESS_TOKEN:
                assertEquals(token.getToken(), value.textValue());
                break;
            case EXPIRES_IN:
                assertEquals(token.getExpiresIn(), value.asInt());
                break;
            case REFRESH_TOKEN:
                assertEquals(token.getRefreshToken(), value.textValue());
                break;
            case TOKEN_TYPE:
                assertEquals(token.getTokenType(), value.textValue());
                break;
            default:
                fail(String.format("Unknown json property : %s", entry.getKey()));
        }
    }

    private SessionKeys matchEntryToEnum(String key) {
        for (SessionKeys value : SessionKeys.values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        fail(String.format("Unknown json property : %s", key));
        return null;
    }

}