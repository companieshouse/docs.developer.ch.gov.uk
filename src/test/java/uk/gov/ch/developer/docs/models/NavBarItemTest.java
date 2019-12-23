package uk.gov.ch.developer.docs.models;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NavBarItemTest {


    private static final String HEADING = "Heading";
    private static final String URL = "URL";

    @Test
    void navBarItem_Constructs_test() {
        NavBarItem item = new NavBarItem(
                HEADING, URL
        );
        assertEquals(HEADING, item.getHeading());
        assertEquals(URL, item.getUrl());
        assertNull(item.getParent());
        assertThat(item.getChildren(), is(empty()));
    }

    @Test
    void navBarItem_Add_UpdatesParentAndChildReferences_test() {
        NavBarItem parent = new NavBarItem(
                HEADING, URL
        );
        NavBarItem child = parent.add(HEADING, URL);
        assertEquals(parent, child.getParent());
        assertThat(parent.getChildren(), contains(child));
        assertThat(parent.getChildren(), hasSize(1));
    }

    @Test
    void navBarItem_getDepth_returnsCorrectDepth() {
        NavBarItem parent = new NavBarItem(
                HEADING, URL
        );
        NavBarItem child = parent.add(HEADING, URL);
        NavBarItem grandChild = child.add(HEADING, URL);
        NavBarItem grandGrandChild = grandChild.add(HEADING, URL);
        assertEquals(0, parent.getDepth());
        assertEquals(1, child.getDepth());
        assertEquals(2, grandChild.getDepth());
        assertEquals(3, grandGrandChild.getDepth());
    }


    @Test
    void navBarItem_checkedUrl_returnsSlashIfUrlIsNull() {
        NavBarItem item = new NavBarItem(
                HEADING, null
        );
        assertEquals("/", item.getUrl());
    }

    @Test
    void navBarItem_checkedUrl_returnsSlashIfUrlIsEmpty() {
        NavBarItem item = new NavBarItem(
                HEADING, ""
        );
        assertEquals("/", item.getUrl());
    }

    @Test
    void navBarItem_checkedUrl_returnsSlashIfUrlIsBlankString() {
        NavBarItem item = new NavBarItem(
                HEADING, "      "
        );
        assertEquals("/", item.getUrl());
    }
}