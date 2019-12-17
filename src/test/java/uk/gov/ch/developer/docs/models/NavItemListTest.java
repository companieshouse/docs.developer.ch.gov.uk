package uk.gov.ch.developer.docs.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;
import org.junit.jupiter.api.Test;

class NavItemListTest {

    private static final String HEADING = "heading";
    private static final String URL = "url";

    @Test
    void NavItemList_Add_CreatesItem_WithCorrectValues_test() {
        NavItemList list = new NavItemList();
        NavBarItem item = list.add(HEADING, URL);
        assertEquals(HEADING, item.getHeading());
        assertEquals(URL, item.getUrl());
    }

    @Test
    void NavItemList_CreatedItems_CanBeAccessed_test() {
        NavItemList list = new NavItemList();
        NavBarItem item = list.add(HEADING, URL);
        Iterator<INavBarItem> iter = list.iterator();
        assertEquals(item, iter.next());
        assertFalse(iter.hasNext());
    }


    @Test
    void NavItemList_CreatedItems_CanBeAccessedInAnOrderedWay_test() {
        NavItemList list = new NavItemList();
        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i);
            NavBarItem item = list.add(value, value);
        }
        Iterator<INavBarItem> iter = list.iterator();

        for (int i = 0; i < 20; i++) {
            String value = String.valueOf(i);
            INavBarItem item = iter.next();
            assertEquals(value, item.getHeading());
            assertEquals(value, item.getUrl());
        }
        assertFalse(iter.hasNext());
    }
}