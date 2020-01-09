package uk.gov.ch.developer.docs.models.user;

import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

public class UserModel implements IUserModel {

    private SignInInfo signIn;
    private UserProfile userProfile;

    public String getId() throws IllegalAccessException {
        runIfSignedIn(
                () -> null
        );
    }

    private void runIfSignedIn(Object o) {
    }

    private String getString() {
        String ret = signIn.getUserProfile().getId();
        return ret != null ? ret : signIn.getUserProfile().getEmail();
    }

    public String getEmail() throws IllegalAccessException {
        throwIfNotSignedIn();
        return signIn.getUserProfile().getEmail();
    }

    public boolean isSignedIn() {
        return signIn != null && signIn.isSignedIn();
    }

    public IUserModel populateUserDetails(final Session sessionData) {
        signIn = sessionData.getSignInInfo();
        return this;
    }

    public IUserModel clear() {
        signIn = null;
        return this;
    }

    private void throwIfNotSignedIn() throws IllegalAccessException {
        if (!isSignedIn()) {
            throw new IllegalAccessException("User logged out");
        }
    }
}
