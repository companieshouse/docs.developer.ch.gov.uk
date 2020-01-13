package uk.gov.ch.developer.docs.models.nav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic implementation of the INavBarItem.
 */
public class NavBarItem implements INavBarItem {

    private final String DEFAULT_URL = System.getProperties().getProperty("home.url");

    private final String heading;
    private final String url;
    private final List<NavBarItem> children;
    private NavBarItem parent;
    private boolean requiresLoggedIn;

    NavBarItem(final String heading, final String url, boolean requiresLoggedIn) {
        this.heading = heading;
        this.url = checkedUrl(url);
        this.requiresLoggedIn = requiresLoggedIn;
        this.children = new ArrayList<>();
    }

    private NavBarItem(final String heading, final String url, boolean requiresLoggedIn,
            final NavBarItem parent) {
        this(heading, url, requiresLoggedIn);
        this.parent = parent;
        parent.children.add(this);
    }

    /**
     * Checks that the given url is valid and returns the default link location if it is not.
     *
     * @param originalUrl the url to validate.
     * @return originalUrl if the url is valid or else the default url.
     */
    private String checkedUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return DEFAULT_URL;
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
        this.requiresLoggedIn = true;
        return this;
    }

    /**
     * Method to assist in creating Navigation models in an easily readable way. Sets
     * requiresLoggedIn to <code>true</code>.
     *
     * @return this so that further operations can be chained.
     */
    @SuppressWarnings("WeakerAccess")
    public NavBarItem doNotrequireLoggedInUser() {
        this.requiresLoggedIn = false;
        return this;
    }

    /**
     * Method to assist in creating Navigation models in an easily readable way. Creates new
     * NavBarItem and inserts it into its children. Inherits fields that are not requested.
     *
     * @return new child.
     */
    public NavBarItem add(String heading, String url) {
        return new NavBarItem(heading, url, requiresLoggedIn, this);
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
     * @inheritDoc
     */
    @Override
    public List<INavBarItem> getChildren() {
        return Collections.unmodifiableList(children);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoggedInOnly() {
        return requiresLoggedIn;
    }
}