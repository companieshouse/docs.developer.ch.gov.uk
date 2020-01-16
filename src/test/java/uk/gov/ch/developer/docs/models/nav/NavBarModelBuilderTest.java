package uk.gov.ch.developer.docs.models.nav;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@ExtendWith(MockitoExtension.class)
class NavBarModelBuilderTest {

    @Mock
    private ModelMap mockModel;
    @Mock
    private IUserModel mockUser;

    @Nested
    @DisplayName("Add Heading Tests:")
    class addTests {

        @Test
        @DisplayName("Creates new list if heading doesn't exist.")
        void NavBarModel_addHeading_CreatesAList_test() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading("Test", DisplayRestrictions.none());
            assertNotNull(created);
        }

        @Test
        @DisplayName("Returns existing list if heading exists.")
        void NavBarModel_addHeading_ReturnsTheSameValues_ForTheSameInput_test() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading("Test", DisplayRestrictions.none());
            NavItemList retrieved = model.addHeading("Test", DisplayRestrictions.none());
            assertEquals(created, retrieved);
        }

        @Test
        @DisplayName("Doesn't just always return the same list.")
        void NavBarModel_addHeading_ReturnsDifferentValues_ForDifferentInputs_test() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading("Test", DisplayRestrictions.none());
            NavItemList retrieved = model
                    .addHeading("TestB", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertNotEquals(created, retrieved);
        }
    }


    @Nested
    @DisplayName("Get Flag Tripped Tests:")
    class currentRestrictionTests {

        @Test
        @DisplayName("Returns None if no user.")
        void noCurrentFlagsTripped_forLoggedOutUser_test() {
            when(mockModel.getAttribute("user")).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            EnumSet<DisplayRestrictions> returned = navModelBuilder
                    .getCurrentRestrictions(mockModel);

            assertThat(returned, hasSize(0));
        }

        @Test
        @DisplayName("Returns None if null user.")
        void noCurrentFlagsTripped_forNullUser_test() {
            when(mockModel.getAttribute("user")).thenReturn(null);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            EnumSet<DisplayRestrictions> returned = navModelBuilder
                    .getCurrentRestrictions(mockModel);

            assertThat(returned, hasSize(0));
        }

        @Test
        @DisplayName("Returns None and User Required if a user is logged in.")
        void userFlagTripped_test() {
            when(mockModel.getAttribute("user")).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(true);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            EnumSet<DisplayRestrictions> returned = navModelBuilder
                    .getCurrentRestrictions(mockModel);

            assertThat(returned, hasSize(1));
            assertThat(returned, containsInAnyOrder(DisplayRestrictions.USER_REQUIRED));
        }
    }

    @Nested
    @DisplayName("End To End Tests: ")
    class endToEndTests {

        @Test
        @DisplayName("Does Builder create navigation bar.")
        void createNavBar() {
            when(mockModel.getAttribute("user")).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();
            NavItemList userOnly = navModelBuilder
                    .addHeading("UserOnly", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            userOnly.add("test", "url");

            NavItemList allPermissions = navModelBuilder
                    .addHeading("AllPermissions", DisplayRestrictions.none());
            allPermissions.add("test", "url");

            NavBarModel navModel = navModelBuilder.build(mockModel);
            Map<String, NavItemList> sections = navModel.getSections();

            assertThat(sections.keySet(), hasSize(1));
            assertThat(sections.keySet(), contains("AllPermissions"));
        }

        @Test
        @DisplayName("Does not return sub menu items that aren't drawable")
        void filterSubMenu_test() {
            when(mockModel.getAttribute("user")).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            NavItemList allPermissions = navModelBuilder
                    .addHeading("AllPermissions", DisplayRestrictions.none());
            NavBarItem noUserItem = allPermissions.add("no user", "url");
            NavBarItem userItem = allPermissions.add("user", "url").requireLoggedInUser();

            NavBarModel navModel = navModelBuilder.build(mockModel);
            Map<String, NavItemList> sections = navModel.getSections();

            assertThat(sections.keySet(), hasSize(1));
            assertThat(sections.keySet(), contains("AllPermissions"));

            Iterator<INavBarItem> iter = sections.get("AllPermissions").iterator();

            ArrayList<INavBarItem> subItems = new ArrayList<>();
            assertTrue(iter.hasNext());//Has at least one value
            while (iter.hasNext()) {
                subItems.add(iter.next());
            }
            for (INavBarItem item : subItems) {
                System.out.println(item.getHeading());
                assertFalse(item.isLoggedInOnly());
            }
            System.out.println(subItems);
            assertThat(subItems, hasSize(1));
            assertEquals("no user", subItems.get(0).getHeading());
        }


        @Test
        @DisplayName("Simulated real case for logged out user.")
        void simulatedRealCase_forLoggedOut_test()
                throws IllegalAccessException, NoSuchFieldException {

            NavBarModelBuilder builderWithMocks = new NavBarModelBuilder();

            NavItemList manageApplications = builderWithMocks
                    .addHeading("Manage Applications",
                            EnumSet.of(DisplayRestrictions.USER_REQUIRED));

            manageApplications.add("View all applications", "example.com");
            manageApplications.add("Add an application", "example.com");

            NavItemList documentation = builderWithMocks
                    .addHeading("General Documentation", DisplayRestrictions.none());
            documentation.add("Get Started", "gettingStartedURL");
            documentation.add("Companies House REST API overview", "homeURL");
            documentation.add("How to add an API key", "homeURL");
            documentation.add("Developer Guidelines", "devGuideURL");

            NavItemList manageAccounts = builderWithMocks
                    .addHeading("Manage account", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            manageAccounts.add("Manage Profile", "homeURL");
            manageAccounts.add("Change Password", "homeURL");

            NavItemList help = builderWithMocks
                    .addHeading("Help", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            help.add("Developer Hub Forum", "homeURL");

            ModelMap userModel = addUserToModel(new ModelMap());

            NavBarModel outWithUser = builderWithMocks.build(userModel);
            Map<String, NavItemList> map_withUser = outWithUser.getSections();
            assertThat(map_withUser.keySet(), hasSize(4));
            assertThat(map_withUser.keySet(),
                    containsInRelativeOrder("Manage Applications", "General Documentation",
                            "Manage account", "Help"));
        }


        @Test
        @DisplayName("Simulated real case for logged in user.")
        void simulatedRealCase_forLoggedIn_test() {

            NavBarModelBuilder builderWithMocks = new NavBarModelBuilder();

            NavItemList manageApplications = builderWithMocks
                    .addHeading("Manage Applications",
                            EnumSet.of(DisplayRestrictions.USER_REQUIRED));

            manageApplications.add("View all applications", "example.com");
            manageApplications.add("Add an application", "example.com");

            NavItemList documentation = builderWithMocks
                    .addHeading("General Documentation", DisplayRestrictions.none());
            documentation.add("Get Started", "gettingStartedURL");
            documentation.add("Companies House REST API overview", "homeURL");
            documentation.add("How to add an API key", "homeURL");
            documentation.add("Developer Guidelines", "devGuideURL");

            NavItemList manageAccounts = builderWithMocks
                    .addHeading("Manage account", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            manageAccounts.add("Manage Profile", "homeURL");
            manageAccounts.add("Change Password", "homeURL");

            NavItemList help = builderWithMocks
                    .addHeading("Help", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            help.add("Developer Hub Forum", "homeURL");

            NavBarModel out = builderWithMocks.build(new ModelMap());
            Map<String, NavItemList> map = out.getSections();
            assertThat(map.keySet(), hasSize(1));
            assertThat(map.keySet(), containsInRelativeOrder("General Documentation"));
        }

        private ModelMap addUserToModel(ModelMap modelMap)
                throws NoSuchFieldException, IllegalAccessException {
            UserModel ret = new UserModel();
            final Field sI = ret.getClass().getDeclaredField("signIn");

            UserProfile up = new UserProfile();
            up.setEmail("Test@gmail.com");

            final SignInInfo signInInfo = new SignInInfo();
            signInInfo.setSignedIn(true);
            signInInfo.setUserProfile(up);

            sI.setAccessible(true);
            sI.set(ret, signInInfo);
            modelMap.addAttribute("user", ret);
            return modelMap;
        }
    }

    @Nested
    class cloningTests {

        @Test
        @DisplayName("Clone Item returns null for not visible item.")
        void cloneItem_returnsNull_IfItemIsntVisible_test() {
            NavBarItem navBarItem = new NavBarItem("heading", "url",
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, DisplayRestrictions.none());
            assertNull(clonedItem);
        }

        @Test
        @DisplayName("Clone Item returns new item with correct heading and url for visible item.")
        void cloneItem_returnsMatchingItem_IfItemIsntVisible_test() {
            NavBarItem navBarItem = new NavBarItem("heading", "url",
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertNotNull(clonedItem);
            assertNotEquals(navBarItem, clonedItem);
            assertEquals(navBarItem.getHeading(), clonedItem.getHeading());
            assertEquals(navBarItem.getUrl(), clonedItem.getUrl());
        }

        @Test
        @DisplayName("Clone Item returns item with cloned children.")
        void cloneItem_returnsMatchingItem_WithChildren_IfChildrenAreVisible_test() {
            NavBarItem navBarItem = new NavBarItem("heading", "url", DisplayRestrictions.none());
            NavBarItem child = navBarItem.add("childHeading", "childUrl");
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, DisplayRestrictions.none());
            assertThat(clonedItem.getChildren(), hasSize(1));
            INavBarItem clonedChild = clonedItem.getChildren().get(0);
            assertNotEquals(child, clonedChild);
            assertEquals(child.getHeading(), clonedChild.getHeading());
            assertEquals(child.getUrl(), clonedChild.getUrl());
        }

        @Test
        @DisplayName("Clone Item returns item without non visible children.")
        void cloneItem_returnsMatchingItem_WithoutChildren_IfChildrenArentVisible_test() {
            NavBarItem navBarItem = new NavBarItem("heading", "url", DisplayRestrictions.none());
            NavBarItem child = navBarItem.add("childHeading", "childUrl");
            child.requireLoggedInUser();
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, DisplayRestrictions.none());
            assertThat(clonedItem.getChildren(), hasSize(0));
        }

        @Test
        @DisplayName("Clone List returns null if no children")
        void cloneList_returnsNullIfNoChildren_test() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarModelBuilder builder = new NavBarModelBuilder();
            NavItemList clonedList = builder.cloneListIfVisible(list, DisplayRestrictions.none());
            assertNull(clonedList);
        }

        @Test
        @DisplayName("Clone List returns a new List with cloned children")
        void cloneList_returnsNewList_IfChildrenAreVisible_test() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarItem item = list.add("heading", "url");
            NavBarModelBuilder builder = new NavBarModelBuilder();
            NavItemList clonedList = builder.cloneListIfVisible(list, DisplayRestrictions.none());
            assertNotNull(clonedList);
            assertNotEquals(list, clonedList);
            Iterator<INavBarItem> iter = clonedList.iterator();
            INavBarItem clonedItem = iter.next();
            assertNotEquals(item, clonedItem);
            assertEquals(item.getHeading(), clonedItem.getHeading());
            assertEquals(item.getUrl(), clonedItem.getUrl());
        }

        @Test
        @DisplayName("Clone List returns null if no children are visible")
        void cloneList_returnsNull_IfNoChildren_areVisible_test() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarItem item = list.add("heading", "url");
            item.requireLoggedInUser();
            NavBarModelBuilder builder = new NavBarModelBuilder();
            NavItemList clonedList = builder.cloneListIfVisible(list, DisplayRestrictions.none());
            assertNull(clonedList);
        }
    }
}