package uk.gov.ch.developer.docs.models.user;

import uk.gov.companieshouse.session.Session;

/**
 * Model to transfer user details between controller and view.
 */
public interface IUserModel {

    /**
     * Returns the selected unique identifer for this user.
     *
     * @return current user id.
     * @throws IllegalAccessException if user not logged in. {@inheritDoc}
     */
    String getId() throws IllegalAccessException;

    /**
     * Return the email address for this user.
     *
     * @return current user email address.
     * @throws IllegalAccessException if user not logged in. {@inheritDoc}
     */
    String getEmail() throws IllegalAccessException;

    /**
     * Checks if user is logged in.
     *
     * @return <code>true</code> if user logged in, otherwise <code>false</code>.
     * {@inheritDoc}
     */
    boolean isSignedIn();

    /**
     * Method used to update user model with values taken from session context.
     *
     * @return this <code>IUserModel</code>. {@inheritDoc}
     */
    IUserModel populateUserDetails(Session session);

    /**
     * Method used to signout and invalidate this IUserModel.
     *
     * @return this <code>IUserModel</code>. {@inheritDoc}
     */
    IUserModel clear();
}
