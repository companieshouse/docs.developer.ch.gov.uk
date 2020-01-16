package uk.gov.ch.developer.docs.models.nav;

import java.util.EnumSet;
import java.util.List;

/**
 * Item to represent one selection from a navigation bar.
 */
public interface INavBarItem {

    /**
     * Retrieve the label of this object.
     *
     * @return the label of this item.
     */
    String getHeading();

    /**
     * Retrieve the link url of this object.
     *
     * @return the url of this item as a String.
     */
    String getUrl();

    /**
     * If this item is a child return the item which it is nested under.
     *
     * @return 0 if not child of another item. Otherwise return depth of parent +1.
     */
    NavBarItem getParent();

    /**
     * Navigation Items that are nested underneath this object.
     *
     * @return Unmodifiable list of children
     */
    List<INavBarItem> getChildren();

    /**
     * Navigation Items that are nested underneath this object, which are drawable given the model
     * attributes.
     *
     * @return Unmodifiable list of children
     */
    List<INavBarItem> getChildren(final EnumSet<DisplayRestrictions> restrictions);

    /**
     * Returns the number of parents are chained until the highest level.
     *
     * @return 0 based integer that counts the number of nested parents.
     */
    int getDepth();

    /**
     * Retrieves set of flags that must be valid in order for this to be visible.
     *
     * @return EnumSet of DisplayRestrictions. An empty set means always visible.
     */
    EnumSet<DisplayRestrictions> getRestrictions();

    /**
     * Given the current visibility restrictions, can this item be displayed.
     *
     * @param restrictions set of flags which are currently valid
     * @return true if visible. Otherwise false.
     */
    boolean isVisible(final EnumSet<DisplayRestrictions> restrictions);
}
