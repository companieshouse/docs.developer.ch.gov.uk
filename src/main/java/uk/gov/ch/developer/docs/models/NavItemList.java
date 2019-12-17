package uk.gov.ch.developer.docs.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class NavItemList implements Iterable<INavBarItem> {

    private List<INavBarItem> list = new ArrayList<>();

    public NavBarItem add(String heading, String url) {
        NavBarItem newItem = new NavBarItem(heading, url);
        list.add(newItem);
        return newItem;
    }

    public Iterator<INavBarItem> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }
}
