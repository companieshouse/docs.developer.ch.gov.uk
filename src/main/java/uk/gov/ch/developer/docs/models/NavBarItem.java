package uk.gov.ch.developer.docs.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NavBarItem implements INavBarItem {

    private final String heading;
    private final String url;
    private final List<NavBarItem> children;
    private NavBarItem parent;

    NavBarItem(final String heading, final String url) {
        this.heading = heading;
        this.url = checkedUrl(url);
        this.children = new ArrayList<>();
    }

    private NavBarItem(final String heading, final String url, final NavBarItem parent) {
        this(heading, url);
        this.parent = parent;
        parent.children.add(this);
    }

    @Override
    public String getHeading() {
        return heading;
    }

    @Override
    public String getUrl() {
        return url;
    }

    private static String checkedUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.trim().isEmpty()) {
            return "/";
        }
        return originalUrl;
    }

    @Override
    public NavBarItem getParent() {
        return parent;
    }

    @Override
    public List<INavBarItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public NavBarItem add(String heading, String url) {
        return new NavBarItem(heading, url, this);
    }

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
}
