package uk.gov.ch.oauth;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileResponse extends uk.gov.companieshouse.session.model.UserProfile {

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

}
