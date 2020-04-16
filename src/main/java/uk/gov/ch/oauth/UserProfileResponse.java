package uk.gov.ch.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.UserProfile;

/**
 * Extension of {@link uk.gov.companieshouse.session.model.UserProfile} to allow addition of {@link
 * JsonProperty} annotations to enable parsing of user data from the authentication server. Most
 * methods delegate directly to the superclass. To provide resilience unused or unknown properties
 * are marked as ignored for default JSON parsing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileResponse extends UserProfile {

    /**
     * @param email email address of user if provided
     */
    @JsonProperty("email")
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    /**
     * @param forename forename of user if provided
     */
    @JsonProperty("forename")
    @Override
    public void setForename(String forename) {
        super.setForename(forename);
    }

    @JsonProperty("id")
    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @JsonProperty("locale")
    @Override
    public void setLocale(String locale) {
        super.setLocale(locale);
    }

    @JsonProperty("scope")
    @Override
    public void setScope(String scope) {
        super.setScope(scope);
    }

    @JsonProperty("surname")
    @Override
    public void setSurname(String surname) {
        super.setSurname(surname);
    }

    @JsonProperty("permissions")
    @Override
    public void setPermissions(final Map<String, Boolean> permissions) {
        super.setPermissions(permissions);
    }

    /**
     * This method adds user profile values to an existing session data map. User profiles in {@link
     * uk.gov.companieshouse.session.Session} are stored as separate key value pairs rather than as
     * a structured object. The values that are consumed by the session are added to the supplied
     * map using appropriate {@link SessionKeys} to map from instance values to internal map values.
     * <br /> N.B. This method does not mutate the instance, it appends data to the supplied map.
     *
     * @param signInData to which the user profile data is to be added
     */
    public void setUserProfile(final Map<String, Object> signInData) {

        final Map<String, Object> userProfileData = new HashMap<>();

        userProfileData.put(SessionKeys.EMAIL.getKey(), getEmail());
        userProfileData.put(SessionKeys.USER_ID.getKey(), getId());
        userProfileData.put(SessionKeys.LOCALE.getKey(), getLocale());
        userProfileData.put(SessionKeys.SCOPE.getKey(), getScope());
        userProfileData.put(SessionKeys.FORENAME.getKey(), getForename());
        userProfileData.put(SessionKeys.SURNAME.getKey(), getSurname());
        userProfileData.put(SessionKeys.PERMISSIONS.getKey(), getPermissions());

        signInData.put(SessionKeys.USER_PROFILE.getKey(), userProfileData);
    }

}
