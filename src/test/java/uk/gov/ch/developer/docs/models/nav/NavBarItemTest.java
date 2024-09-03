package uk.gov.ch.developer.docs.models.nav;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

class NavBarItemTest {

    private static final String HEADING = "Heading";
    private static final String URL = "URL";
    private static final String DEFAULT_URL = "/";

    @BeforeAll
    static void injectProperties() {
        Properties p = System.getProperties();
        p.setProperty("home.url", DEFAULT_URL);
    }

    @AfterAll
    static void cleanUp() {
        System.getProperties().setProperty("home.url", "");
    }

    @Nested
    @DisplayName("NavBarItem constructor tests:")
    class constructorTests {

        @Test
        @DisplayName(" - sunny day test")
        void navBarItem_Constructs_test() {
            NavBarItem item = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertTrue(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            assertEquals(HEADING, item.getHeading());
            assertEquals(URL, item.getUrl());
            assertNull(item.getParent());
            assertThat(item.getChildren(), is(empty()));

            item = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            assertFalse(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            assertEquals(HEADING, item.getHeading());
            assertEquals(URL, item.getUrl());
            assertNull(item.getParent());
            assertThat(item.getChildren(), is(empty()));
        }

        @ParameterizedTest(name = "- replaces null url")
        @NullSource
        void navBarItem_Constructor_replacesURLWithDefault_IfNull(final String url) {
            assertDefaultUrlIsReturned(url);
        }

        @ParameterizedTest(name = "- replaces {1} url")
        @CsvSource(value = {"'':empty","'      ':string of spaces"}, delimiter = ':')
        void navBarItem_Constructor_replacesURLWithDefault_If(final String url, final String description) {
            assertDefaultUrlIsReturned(url);
        }

        private void assertDefaultUrlIsReturned(final String url) {
            final NavBarItem item = new NavBarItem(HEADING, url, DisplayRestrictions.none());
            assertEquals(DEFAULT_URL, item.getUrl());
        }

    }

    @Nested
    @DisplayName("Child-Parent relationship tests:")
    class parentChildTests {

        @Test
        @DisplayName("NavBarItem add - constructs child and sets relations")
        void navBarItem_Add_UpdatesParentAndChildReferences_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarItem child = parent.add(HEADING, URL);
            assertTrue(child.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            assertEquals(parent, child.getParent());
            assertThat(parent.getChildren(), contains(child));
            assertThat(parent.getChildren(), hasSize(1));
        }

        @Test
        @DisplayName("NavBarItem tracks depth for varying relationships")
        void navBarItem_getDepth_returnsCorrectDepth_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarItem child = parent.add(HEADING, URL);
            NavBarItem grandChild = child.add(HEADING, URL);
            NavBarItem grandGrandChild = grandChild.add(HEADING, URL);
            assertEquals(0, parent.getDepth());
            assertEquals(1, child.getDepth());
            assertEquals(2, grandChild.getDepth());
            assertEquals(3, grandGrandChild.getDepth());
        }

        @Test
        @DisplayName("Get Children with restrictions returns visible children")
        void navBarItem_getChildren_withRestrictions_returnsValid_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            NavBarItem child = parent.add(HEADING, URL);
            final List<INavBarItem> children = parent.getChildren(DisplayRestrictions.none());
            assertThat(children, hasSize(1));
            assertThat(children, contains(child));
        }

        @Test
        @DisplayName("Get Children with restrictions doesn't return non visible children")
        void navBarItem_getChildren_withRestrictions_Doesnt_ReturnsInvalid_test() {
            NavBarItem parent = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarItem child = parent.add(HEADING, URL);
            final List<INavBarItem> children = parent.getChildren(DisplayRestrictions.none());
            assertThat(children, hasSize(0));
        }
    }

    @Nested
    @DisplayName("Visibility tests:")
    class visibilityTests {

        @Test
        @DisplayName("Is Visible Returns True for any settings, if display is None")
        void isVisible_Returns_True_ForAnySettings() {
            NavBarItem item = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            assertTrue(item.isVisible(EnumSet.of(DisplayRestrictions.USER_REQUIRED)));
            assertTrue(item.isVisible(DisplayRestrictions.none()));
        }


        @Test
        @DisplayName("Get Restrictions returns the expected values.")
        void getRestrictions_Returns_correctValues() {
            NavBarItem item = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertThat(item.getRestrictions(), hasSize(1));
            assertThat(item.getRestrictions(), contains(DisplayRestrictions.USER_REQUIRED));

            item = new NavBarItem(HEADING, URL,
                    DisplayRestrictions.none());
            assertThat(item.getRestrictions(), hasSize(0));
            assertThat(item.getRestrictions(), not(contains(DisplayRestrictions.USER_REQUIRED)));
        }

        @Test
        @DisplayName("Is Visible Returns True for User Logged In settings, if User logged in")
        void isVisible_Returns_True_ForAnyUser_WhenUserLoggedIn() {
            NavBarItem item = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertTrue(item.isVisible(EnumSet.of(DisplayRestrictions.USER_REQUIRED)));
        }

        @Test
        @DisplayName("Is Visible Returns false for User Logged In settings, if User logged out")
        void isVisible_Returns_False_ForUser_WhenUserLoggedOut() {
            NavBarItem item = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertFalse(item.isVisible(DisplayRestrictions.none()));
        }
    }

    @Nested
    @DisplayName("NavBarItem access tests:")
    class accessTests {

        @Test
        @DisplayName("- sets false to true")
        void navBarItem_requireLoggedIn_Sets_isLoggedInOnly_ToTrue_test() {
            NavBarItem item = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            assertFalse(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            item.requireLoggedInUser();
            assertTrue(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
        }

        @Test
        @DisplayName("- sets true to false")
        void navBarItem_doNotRequireLoggedIn_Sets_isLoggedInOnly_ToFalse_test() {
            NavBarItem item = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertTrue(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            item.doNotRequireLoggedInUser();
            assertFalse(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
        }
    }

}