package uk.gov.ch.developer.docs.models.nav;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to construct and order menu items.
 */
public class NavBarModel {

    private Map<String, NavItemList> sections = new LinkedHashMap<>();

    /**
     * Retrieves map of all headings and their Navigation Item Lists. Used by Thymeleaf.
     * @return Map of current sections.
     */
    @SuppressWarnings("WeakerAccess")
    public Map<String, NavItemList> getSections() {
        return sections;
    }

    /**
     * Retrieve heading that matches the key, or creates and returns a new one.
     *
     * @param heading String that acts as key and display title for user.
     * @param defaultRequiresLoggedIn if a new heading is created, does it require a logged in user
     * to see by default.
     * @return NavItemList or null if no value is found.
     */
    public NavItemList addHeading(final String heading,
            final UserRequired defaultRequiresLoggedIn) {
        return sections.computeIfAbsent(
                heading,
                k -> new NavItemList(defaultRequiresLoggedIn.isRequired())
        );
    }

    /**
     * Retrieve heading that matches the key, or returns null if no matching list is found.
     *
     * @param heading String that acts as key and display title for user.
     * @return NavItemList or null if no value is found.
     */
    NavItemList getHeading(final String heading) {
        return sections.get(heading);
    }
}
