package uk.gov.ch.developer.docs.models.nav;

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
     * @return List of children
     */
    List<INavBarItem> getChildren();

    /**
     * Returns the number of parents are chained until the highest level.
     * @return 0 based integer that counts the number of nested parents.
     */
    int getDepth();

    /**
     * Checks if this link should only be displayed if a user is logged in.
     *
     * @return <code>true</code> if it should only
     */
    boolean isLoggedInOnly();
}
