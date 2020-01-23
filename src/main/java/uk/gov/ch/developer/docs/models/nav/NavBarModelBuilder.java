package uk.gov.ch.developer.docs.models.nav;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
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
     * @param restrictions if a new heading is created, does it require conditions to be met to be
     * seen.
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
     * Retrieve heading that matches the key, or creates and returns a new one.
     *
     * @param heading String that acts as key and display title for user.
     * @param restrictions if a new heading is created, does it require conditions to be met to be
     * seen.
     * @return NavItemList or null if no value is found.
     */
    public NavItemList addHeading(final String heading,
            final DisplayRestrictions... restrictions) {
        EnumSet<DisplayRestrictions> set = EnumSet.noneOf(DisplayRestrictions.class);
        Collections.addAll(set, restrictions);
        return addHeading(heading, set);
    }

    /**
     * Identifies what restrictions apply to this modelMap.
     *
     * @return Returns an EnumSet that contains all restrictions that pass for the model.
     */
    EnumSet<DisplayRestrictions> getCurrentRestrictions(final ModelMap modelMap) {
        final EnumSet<DisplayRestrictions> satisfiedRestrictions = EnumSet
                .noneOf(DisplayRestrictions.class);
        final EnumSet<DisplayRestrictions> allRestrictions = EnumSet
                .allOf(DisplayRestrictions.class);
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
        final LinkedHashMap<String, NavItemList> ret = new LinkedHashMap<>();
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
    NavItemList cloneListIfVisible(final NavItemList navItemList,
            final EnumSet<DisplayRestrictions> currentRestrictions) {
        final Iterator<INavBarItem> children = navItemList.iterator();
        final List<INavBarItem> clonedChildren = new LinkedList<>();
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
    INavBarItem cloneItemIfVisible(final INavBarItem value,
            final EnumSet<DisplayRestrictions> currentRestrictions) {
        INavBarItem ret = null;
        if (value.isVisible(currentRestrictions)) {
            final List<INavBarItem> clonedChildren = value.getChildren(currentRestrictions)
                    .stream()
                    .map(child -> cloneItemIfVisible(child, currentRestrictions))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            ret = new NavBarItem(value, clonedChildren);
        }
        return ret;
    }
}
