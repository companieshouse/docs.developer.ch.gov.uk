package uk.gov.ch.developer.docs.models.user;

import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.model.SignInInfo;

public class UserModel implements IUserModel {

    private SignInInfo signIn;

    /**
     * Static method used in thymeleaf to handle null users in a readable fashion.
     *
     * @param user user who is
     * @return <code>true</code> if user is not null and is signed in. Otherwise returns
     * <code>false</code>.
     */
    public static boolean isUserSignedIn(final UserModel user) {
        return user != null && user.isSignedIn();
    }

    public String getEmail() throws IllegalAccessException {
        throwIfNotSignedIn();
        return signIn.getUserProfile().getEmail();
    }

    public boolean isSignedIn() {
        return signIn != null && signIn.isSignedIn();
    }

    public String getId() throws IllegalAccessException {
        throwIfNotSignedIn();
        String ret = signIn.getUserProfile().getId();
        return ret != null ? ret : signIn.getUserProfile().getEmail();
    }

    public IUserModel clear() {
        signIn = null;
        return this;
    }

    public IUserModel populateUserDetails(final Session sessionData) {
        if (sessionData == null) {
            clear();
        } else {
            signIn = sessionData.getSignInInfo();
        }
        return this;
    }

    /**
     * Method used to remove common boilerplate from accessor methods.
     *
     * @throws IllegalAccessException if the user is not signed in.
     */
    private void throwIfNotSignedIn() throws IllegalAccessException {
        if (!isSignedIn()) {
            throw new IllegalAccessException("User not signed in.");
        }
    }
}
