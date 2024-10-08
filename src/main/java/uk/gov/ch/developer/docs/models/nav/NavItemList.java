package uk.gov.ch.developer.docs.models.nav;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Iterable to group related INavBarItems.
 */
public class NavItemList implements Iterable<INavBarItem> {

    private final List<INavBarItem> list;
    private EnumSet<DisplayRestrictions> defaultDisplaySettings;

    NavItemList(final EnumSet<DisplayRestrictions> defaultDisplaySettings) {
        this.defaultDisplaySettings = defaultDisplaySettings;
        list = new LinkedList<>();
    }

    NavItemList(final List<INavBarItem> clonedChildren) {
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
    public NavBarItem add(final String heading, final String url) {
        var newItem = new NavBarItem(heading, url, defaultDisplaySettings.clone());
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
    boolean hasDrawableChildren(final EnumSet<DisplayRestrictions> flagsTripped) {
        return list.stream()
                .anyMatch(item -> item.isVisible(flagsTripped));
    }

}
