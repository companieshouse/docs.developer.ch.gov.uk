package uk.gov.ch.developer.docs.models.nav;

/**
 * Enum made to make NavBarModel creation and config more explicit.
 */
public enum UserRequired {
    USER_REQUIRED(true), USER_NOT_REQUIRED(false);

    private final boolean required;

    UserRequired(boolean b) {
        required = b;
    }

    public boolean isRequired() {
        return required;
    }
}
