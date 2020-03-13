package uk.gov.ch.oauth;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.model.UserProfile;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileResponse extends UserProfile {

    @JsonProperty("email")
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

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
    public void setPermissions(Map<String, Boolean> permissions) {
        super.setPermissions(permissions);
    }
    
    public void setUserProfile(Map<String, Object> signInData) {

        Map<String, Object> userProfileData = new HashMap<>();

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
