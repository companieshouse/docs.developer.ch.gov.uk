package uk.gov.ch.developer.docs.models.nav;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NavBarItemTest {

    private static final String HEADING = "Heading";
    private static final String URL = "URL";
    private static final String DEFAULT_URL = "/";

    @BeforeAll
    static void injectProperties() {
        Properties p = System.getProperties();
        p.setProperty("home.url", DEFAULT_URL);
    }

    @Nested
    @DisplayName("NavBarItem constructor tests:")
    class constructorTests {

        @Test
        @DisplayName(" - sunny day test")
        void navBarItem_Constructs_test() {
            NavBarItem item = new NavBarItem(HEADING, URL, true);
            assertTrue(item.isLoggedInOnly());
            assertEquals(HEADING, item.getHeading());
            assertEquals(URL, item.getUrl());
            assertNull(item.getParent());
            assertThat(item.getChildren(), is(empty()));

            item = new NavBarItem(HEADING, URL, false);
            assertFalse(item.isLoggedInOnly());
            assertEquals(HEADING, item.getHeading());
            assertEquals(URL, item.getUrl());
            assertNull(item.getParent());
            assertThat(item.getChildren(), is(empty()));
        }

        @Test
        @DisplayName("- replaces null url")
        void navBarItem_Constructor_replacesURLWithDefault_IfUrlIsNull_test() {
            NavBarItem item = new NavBarItem(HEADING, null, true);
            assertEquals(DEFAULT_URL, item.getUrl());
        }

        @Test
        @DisplayName("- replaces empty string url")
        void navBarItem_Constructor_replacesURLWithDefault_IfUrlIsEmpty_test() {
            NavBarItem item = new NavBarItem(HEADING, "", true);
            assertEquals(DEFAULT_URL, item.getUrl());
        }

        @Test
        @DisplayName("- replaces string of spaces url")
        void navBarItem_Constructor_replacesURLWithDefault_IfUrlIsBlankString_test() {
            NavBarItem item = new NavBarItem(HEADING, "      ", true);
            assertEquals(DEFAULT_URL, item.getUrl());
        }

    }

    @Nested
    @DisplayName("Child-Parent relationship tests:")
    class parentChildTests {

        @Test
        @DisplayName("NavBarItem add - constructs child and sets relations")
        void navBarItem_Add_UpdatesParentAndChildReferences_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL, true);
            NavBarItem child = parent.add(HEADING, URL);
            assertTrue(child.isLoggedInOnly());
            assertEquals(parent, child.getParent());
            assertThat(parent.getChildren(), contains(child));
            assertThat(parent.getChildren(), hasSize(1));
        }

        @Test
        @DisplayName("NavBarItem tracks depth for varying relationships")
        void navBarItem_getDepth_returnsCorrectDepth_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL, true);
            NavBarItem child = parent.add(HEADING, URL);
            NavBarItem grandChild = child.add(HEADING, URL);
            NavBarItem grandGrandChild = grandChild.add(HEADING, URL);
            assertEquals(0, parent.getDepth());
            assertEquals(1, child.getDepth());
            assertEquals(2, grandChild.getDepth());
            assertEquals(3, grandGrandChild.getDepth());
        }

    }

    @Nested
    @DisplayName("NavBarItem access tests:")
    class accessTests {

        @Test
        @DisplayName("- sets false to true")
        void navBarItem_requireLoggedIn_Sets_isLoggedInOnly_ToTrue_test() {
            NavBarItem item = new NavBarItem(HEADING, URL, false);
            assertFalse(item.isLoggedInOnly());
            item.requireLoggedInUser();
            assertTrue(item.isLoggedInOnly());
        }

        @Test
        @DisplayName("- sets true to false")
        void navBarItem_doNotRequireLoggedIn_Sets_isLoggedInOnly_ToFalse_test() {
            NavBarItem item = new NavBarItem(HEADING, URL, true);
            assertTrue(item.isLoggedInOnly());
            item.doNotrequireLoggedInUser();
            assertFalse(item.isLoggedInOnly());
        }
    }

}