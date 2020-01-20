package uk.gov.ch.developer.docs.models.nav;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic implementation of the INavBarItem.
 */
public class NavBarItem implements INavBarItem {

    private final String defaultUrl = "/";

    private final String heading;
    private final String url;
    private final List<INavBarItem> children;
    private final EnumSet<DisplayRestrictions> displaySettings;
    private NavBarItem parent;

    private NavBarItem(final String heading, final String url,
            final EnumSet<DisplayRestrictions> displaySettings,
            final NavBarItem parent) {
        this(heading, url, displaySettings);
        this.parent = parent;
        parent.children.add(this);
    }

    NavBarItem(final String heading, final String url,
            final EnumSet<DisplayRestrictions> displaySettings) {
        this.heading = heading;
        this.url = checkedUrl(url);
        this.displaySettings = displaySettings;
        this.children = new LinkedList<>();
    }

    NavBarItem(final INavBarItem clonedFrom, final List<INavBarItem> clonedChildren) {
        this.displaySettings = DisplayRestrictions.none();
        this.heading = clonedFrom.getHeading();
        this.url = clonedFrom.getUrl();
        this.children = clonedChildren;
    }

    /**
     * Checks that the given url is valid and returns the default link location if it is not.
     *
     * @param originalUrl the url to validate.
     * @return originalUrl if the url is valid or else the default url.
     */
    private String checkedUrl(final String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return defaultUrl;
        }
        return originalUrl;
    }

    /**
     * Method to assist in creating Navigation models in an easily readable way. Sets
     * requiresLoggedIn to <code>true</code>.
     *
     * @return this so that further operations can be chained.
     */
    @SuppressWarnings("WeakerAccess")
    public NavBarItem requireLoggedInUser() {
        this.displaySettings.add(DisplayRestrictions.USER_REQUIRED);
        return this;
    }

    /**
     * Method to assist in creating Navigation models in an easily readable way. Sets
     * requiresLoggedIn to <code>true</code>.
     *
     * @return this so that further operations can be chained.
     */
    @SuppressWarnings("WeakerAccess")
    public NavBarItem doNotRequireLoggedInUser() {
        this.displaySettings.remove(DisplayRestrictions.USER_REQUIRED);
        return this;
    }

    /**
     * Method to assist in creating Navigation models in an easily readable way. Creates new
     * NavBarItem and inserts it into its children. Inherits fields that are not requested.
     *
     * @return new child.
     */
    @SuppressWarnings("WeakerAccess")
    public NavBarItem add(final String heading, final String url) {
        return new NavBarItem(heading, url, displaySettings.clone(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHeading() {
        return heading;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavBarItem getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<INavBarItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<INavBarItem> getChildren(final EnumSet<DisplayRestrictions> restrictions) {
        List<? extends INavBarItem> ret = children.stream()
                .filter(item -> item.isVisible(restrictions)).collect(
                        Collectors.toList());
        return Collections.unmodifiableList(ret);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDepth() {
        int ret;
        if (parent == null) {
            ret = 0;
        } else {
            ret = parent.getDepth() + 1;
        }
        return ret;
    }

    @Override
    public EnumSet<DisplayRestrictions> getRestrictions() {
        return displaySettings;
    }

    /**
     * Checks whether this item should be displayed
     *
     * @param flagsTriggered set of DisplayRestrictions which have passed the criteria to be shown.
     * @return true if, the items display conditions are a subset of the valid flags. Otherwise
     * false.
     */
    @Override
    public boolean isVisible(final EnumSet<DisplayRestrictions> flagsTriggered) {
        return flagsTriggered.containsAll(this.displaySettings);
    }
}