package uk.gov.ch.oauth.tokens;

import static org.apache.naming.ResourceRef.SCOPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.session.SessionKeys;

class UserProfileResponseTest {

    public static final String EMAIL = "EMAIL";
    public static final String FORENAME = "FORENAME";
    public static final String ID = "ID";
    public static final String LOCALE = "LOCALE";
    public static final String SURNAME = "SURNAME";
    private static final Map<String, Object> USER_PROFILE = new HashMap<>();
    private static final Map<String, Boolean> PERMISSIONS = new HashMap<>();
    private UserProfileResponse userProfileResponse;

    @BeforeEach
    void setup() {
        userProfileResponse = new UserProfileResponse();
        userProfileResponse.setEmail(EMAIL);
        userProfileResponse.setForename(FORENAME);
        userProfileResponse.setId(ID);
        userProfileResponse.setLocale(LOCALE);
        userProfileResponse.setPermissions(PERMISSIONS);
        userProfileResponse.setScope(SCOPE);
        userProfileResponse.setSurname(SURNAME);
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Check User Profile serialises with correct keys and values")
    void accessTokenSerialises() throws JsonProcessingException {
        String serialised = mapper.writeValueAsString(userProfileResponse);
        JsonNode tokenAsTree = mapper.readValue(serialised, JsonNode.class);
        final Iterator<Entry<String, JsonNode>> fields = tokenAsTree.fields();
        AtomicInteger fieldCount = new AtomicInteger();
        fields.forEachRemaining(entry -> {
            checkNodeValue(entry, userProfileResponse);
            fieldCount.getAndIncrement();
        });
        assertEquals(7, fieldCount.get());
    }

    @Test
    @DisplayName("Check AccessToken deserializes correctly.")
    void setAccessTokenDeserialises() throws JsonProcessingException {
        String input = "{\"email\":\"EMAIL\",\"forename\":\"FORENAME\",\"id\":\"ID\",\"locale\":\"LOCALE\",\"scope\":\"scope\",\"permissions\":{},\"surname\":\"SURNAME\"}";
        UserProfileResponse parsedToken = mapper.readValue(input, UserProfileResponse.class);
        assertEquals(userProfileResponse.getScope(), parsedToken.getScope());
        assertEquals(userProfileResponse.getPermissions(), parsedToken.getPermissions());
        assertEquals(userProfileResponse.getLocale(), parsedToken.getLocale());
        assertEquals(userProfileResponse.getId(), parsedToken.getId());
        assertEquals(userProfileResponse.getForename(), parsedToken.getForename());
        assertEquals(userProfileResponse.getSurname(), parsedToken.getSurname());
        assertEquals(userProfileResponse.getEmail(), parsedToken.getEmail());
    }

    private void checkNodeValue(Entry<String, JsonNode> entry,
            UserProfileResponse userProfileResponse) {
        System.out.println(entry.getKey());
        SessionKeys fieldKey = matchEntryToEnum(entry.getKey());
        final JsonNode value = entry.getValue();
        switch (fieldKey) {
            case EMAIL:
                assertEquals(userProfileResponse.getEmail(), value.textValue());
                break;
            case FORENAME:
                assertEquals(userProfileResponse.getForename(), value.textValue());
                break;
            case SURNAME:
                assertEquals(userProfileResponse.getSurname(), value.textValue());
                break;
            case USER_ID:
                assertEquals(userProfileResponse.getId(), value.textValue());
                break;
            case LOCALE:
                assertEquals(userProfileResponse.getLocale(), value.textValue());
                break;
            case PERMISSIONS:
                assertTrue(value.isContainerNode());
                break;
            case SCOPE:
                assertEquals(userProfileResponse.getScope(), value.textValue());
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
        fail(String.format("Unknown json property : %sf", key));
        return null;
    }
}