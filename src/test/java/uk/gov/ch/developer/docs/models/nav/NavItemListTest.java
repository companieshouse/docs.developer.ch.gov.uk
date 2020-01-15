package uk.gov.ch.developer.docs.models.nav;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Iterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import uk.gov.ch.developer.docs.models.user.IUserModel;

@ExtendWith(MockitoExtension.class)
class NavItemListTest {

    private static final String HEADING = "heading";
    private static final String URL = "url";
    private static final String ALT_HEADING = "Alternate heading";
    private static final String ALT_URL = "Alternate url";
    @Mock
    private IUserModel mockUser;
    @Mock
    ModelMap mockModelMap;

    @Nested
    @DisplayName("Add Tests:")
    class addTests {

        @Test
        @DisplayName("Items are created with correct values.")
        void NavItemList_Add_CreatesItem_WithCorrectValues_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarItem item = list.add(HEADING, URL);
            assertEquals(HEADING, item.getHeading());
            assertEquals(URL, item.getUrl());
            assertTrue(item.isLoggedInOnly());

            list = new NavItemList(DisplayRestrictions.NONE());
            item = list.add(ALT_HEADING, ALT_URL);
            assertEquals(ALT_HEADING, item.getHeading());
            assertEquals(ALT_URL, item.getUrl());
            assertFalse(item.isLoggedInOnly());
        }
    }

    @Nested
    @DisplayName("Iterator Tests:")
    class iterTests {

        @Test
        @DisplayName("- Items can be retrieved")
        void NavItemList_CreatedItems_CanBeAccessed_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarItem item = list.add(HEADING, URL);
            Iterator<INavBarItem> iter = list.iterator();
            assertEquals(item, iter.next());
            assertFalse(iter.hasNext());
        }

        /**
         * This test has a 1/20! chance of returning a false positive and coincidentally ordering
         * all items in an ordered way even if it is actually unordered.
         */
        @Test
        @DisplayName("- Items are retrieved in ordered fashion")
        void NavItemList_CreatedItems_CanBeAccessedInAnOrderedWay_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
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

    @Nested
    @DisplayName("Has Drawable Children Tests:")
    class drawableTest {

        @Test
        @DisplayName("- Returns False if list is empty")
        void NavItemList_hasDrawableChildren_ReturnsFalse_IfNoChildren_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertFalse(list.hasDrawableChildren(EnumSet.allOf(DisplayRestrictions.class)));
        }

        @Test
        @DisplayName("- Returns True if list has values and doesn't require log in.")
        void NavItemList_hasDrawableChildren_ReturnsTrue_IfChildren_test() {
            NavItemList list = new NavItemList(DisplayRestrictions.NONE());
            list.add(HEADING, URL);
            assertTrue(list.hasDrawableChildren(EnumSet.allOf(DisplayRestrictions.class)));
        }

        @Test
        @DisplayName("- Returns False if list values require log in and user not logged in.")
        void NavItemList_hasDrawableChildren_ReturnsFalse_IfChildrenRequireSignIn_AndUserLoggedOut_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            list.add(HEADING, URL);
            assertFalse(list.hasDrawableChildren(DisplayRestrictions.NONE()));
        }

        @Test
        @DisplayName("- Returns True if list values require log in and user logged in.")
        void NavItemList_hasDrawableChildren_ReturnsTrue_IfChildrenRequireSignIn_AndUserSignedIn_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            list.add(HEADING, URL);
            assertTrue(list.hasDrawableChildren(EnumSet.of(DisplayRestrictions.USER_REQUIRED)));
        }

        @Test
        @DisplayName("- Returns True if list values require log in and user logged in.")
        void NavItemList_hasDrawableChildren_ReturnsTrue_IfChildrenRequireSignIn_AndUserSignedIn_b_test() {
            NavItemList list = new NavItemList(EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            list.add(HEADING, URL);
            assertTrue(list.hasDrawableChildren(EnumSet.of(DisplayRestrictions.USER_REQUIRED)));
        }
    }
}