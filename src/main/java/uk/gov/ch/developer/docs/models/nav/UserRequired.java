package uk.gov.ch.developer.docs.models.nav;

public enum UserRequired {
    userRequired(true), userNotRequired(false);

    private final boolean required;

    UserRequired(boolean b) {
        required = b;
    }

    public boolean isRequired() {
        return required;
    }
}
