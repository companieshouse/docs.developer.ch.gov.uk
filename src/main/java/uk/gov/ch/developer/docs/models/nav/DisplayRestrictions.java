package uk.gov.ch.developer.docs.models.nav;

import java.util.EnumSet;
import java.util.function.Predicate;
import org.springframework.ui.ModelMap;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;

/**
 * Enum made to make NavBarModel creation and config more explicit.
 */
public enum DisplayRestrictions {
    USER_REQUIRED(m -> UserModel.isUserSignedIn((IUserModel) m.getAttribute("user"))),
    NEVER(m -> false);

    private final Predicate<ModelMap> condition;

    DisplayRestrictions(Predicate<ModelMap> test) {
        condition = test;
    }

    public static EnumSet<DisplayRestrictions> NONE() {
        return EnumSet.noneOf(DisplayRestrictions.class);
    }

    /**
     * Method to check if the restriction is satisfied.
     *
     * @param m current parameters on the model.
     * @return true if restriction is satisfied otherwise false.
     */
    public boolean test(ModelMap m) {
        return condition.test(m);
    }
}
