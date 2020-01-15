package uk.gov.ch.developer.docs.models.nav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

/**
 * Iterable to group related INavBarItems.
 */
public class NavItemList implements Iterable<INavBarItem> {

    private EnumSet<DisplayRestrictions> defaultDisplaySettings;
    private List<INavBarItem> list = new ArrayList<>();

    NavItemList(EnumSet<DisplayRestrictions> defaultDisplaySettings) {
        this.defaultDisplaySettings = defaultDisplaySettings;
    }

    NavItemList(ArrayList<INavBarItem> clonedChildren) {
        this.list = clonedChildren;
    }

    /**
     * Adds a new child to this item to be displayed as a sub item in the menu. The child inherits
     * the required signin of this list.
     *
     * @param heading label of the sub menu item.
     * @param url link address of the sub menu item.
     * @return the newly created child item.
     */
    public NavBarItem add(String heading, String url) {
        NavBarItem newItem = new NavBarItem(heading, url, defaultDisplaySettings.clone());
        list.add(newItem);
        return newItem;
    }

    /**
     * {@inheritDoc} Cycles across children at top level without going deeper into tree.
     */
    public Iterator<INavBarItem> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    /**
     * Thymeleaf method used to prevent empty section headers being drawn. This only checks the
     * highest level as its assumed that restrictions on parents will apply to children.
     *
     * @param flagsTripped Restrictions that have been met.
     * @return <code>true</code> if the sub items of this list contains at least one item that
     * should be drawn. Otherwise returns <code>false</code>.
     */
    boolean hasDrawableChildren(EnumSet<DisplayRestrictions> flagsTripped) {
        return list.stream()
                .anyMatch(item -> item.isVisible(flagsTripped));
    }

}
