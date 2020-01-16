package uk.gov.ch.developer.docs.models.nav;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.ui.ModelMap;

/**
 * Builder class for NavBarModels
 */
public class NavBarModelBuilder {


    private final Map<String, NavItemList> sections = new LinkedHashMap<>();

    /**
     * Retrieve heading that matches the key, or creates and returns a new one.
     *
     * @param heading String that acts as key and display title for user.
     * @param restrictions if a new heading is created, does it require a logged in user to see by
     * default.
     * @return NavItemList or null if no value is found.
     */
    //Using No Sonar to disable code smell about relying on EnumSet rather than Set. EnumSet is required.
    public NavItemList addHeading(final String heading,
            final EnumSet<DisplayRestrictions> restrictions) { //NOSONAR
        return sections.computeIfAbsent(
                heading,
                k -> new NavItemList(restrictions)
        );
    }

    /**
     * Identifies what restrictions apply to this modelMap.
     *
     * @return Returns an EnumSet that contains all restrictions that pass for the model.
     */
    EnumSet<DisplayRestrictions> getCurrentRestrictions(ModelMap modelMap) {
        EnumSet<DisplayRestrictions> satisfiedRestrictions = EnumSet
                .noneOf(DisplayRestrictions.class);
        EnumSet<DisplayRestrictions> allRestrictions = EnumSet.allOf(DisplayRestrictions.class);
        for (DisplayRestrictions restriction : allRestrictions) {
            if (restriction.test(modelMap)) {
                satisfiedRestrictions.add(restriction);
            }
        }
        return satisfiedRestrictions;
    }

    /**
     * Constructs a new modelMap according to the model this builder contains. It filters out pages
     * as children become non visible to the restrictions that the model sets.
     *
     * @return NavBarModel to be rendered by for the html.
     */
    public NavBarModel build(final ModelMap modelMap) {
        final EnumSet<DisplayRestrictions> currentRestrictions = getCurrentRestrictions(modelMap);
        LinkedHashMap<String, NavItemList> ret = new LinkedHashMap<>();
        for (Entry<String, NavItemList> entry : sections.entrySet()) {
            NavItemList clone = cloneListIfVisible(entry.getValue(), currentRestrictions);
            if (clone != null) {
                ret.put(entry.getKey(), clone);
            }
        }
        return new NavBarModel(ret);
    }

    /**
     * Creates new NavItemList with filtered children.
     *
     * @param currentRestrictions flags that are currently displayable.
     * @return null if no children to display. Otherwise new list containing visible children.
     */
    NavItemList cloneListIfVisible(NavItemList navItemList,
            EnumSet<DisplayRestrictions> currentRestrictions) {
        Iterator<INavBarItem> children = navItemList.iterator();
        ArrayList<INavBarItem> clonedChildren = new ArrayList<>();
        while (children.hasNext()) {
            INavBarItem clonedChild = cloneItemIfVisible(children.next(), currentRestrictions);
            if (clonedChild != null) {
                clonedChildren.add(clonedChild);
            }
        }
        NavItemList ret;
        if (clonedChildren.isEmpty()) {
            ret = null;
        } else {
            ret = new NavItemList(clonedChildren);
        }
        return ret;
    }

    /**
     * Recursive method to create identical model removing the items that should not be displayed.
     *
     * @return null if item is not displayable. Otherwise similar item with children filtered in the
     * same way.
     */
    INavBarItem cloneItemIfVisible(INavBarItem value,
            EnumSet<DisplayRestrictions> currentRestrictions) {
        INavBarItem ret;
        if (value.isVisible(currentRestrictions)) {
            Iterator<INavBarItem> children = value.getChildren(currentRestrictions).iterator();
            ArrayList<INavBarItem> clonedChildren = new ArrayList<>();
            while (children.hasNext()) {
                INavBarItem clonedChild = cloneItemIfVisible(children.next(), currentRestrictions);
                if (clonedChild != null) {
                    clonedChildren.add(clonedChild);
                }
            }
            ret = new NavBarItem(value, clonedChildren);
        } else {
            ret = null;
        }
        return ret;
    }
}
