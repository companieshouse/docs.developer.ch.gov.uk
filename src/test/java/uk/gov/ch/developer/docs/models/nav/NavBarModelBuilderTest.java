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
import uk.gov.ch.developer.docs.controller.ModelAttributeNames;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@ExtendWith(MockitoExtension.class)
class NavBarModelBuilderTest {

    private static final String TEST = "test";
    private static final String TEST_B = "TestB";
    private static final String HEADING = "heading";
    private static final String CHILD_HEADING = "childHeading";
    private static final String URL = "url";
    private static final String CHILD_URL = "childUrl";
    @Mock
    private ModelMap mockModel;
    @Mock
    private IUserModel mockUser;

    @Nested
    @DisplayName("Add Heading Tests:")
    class AddTests {

        @Test
        @DisplayName("Creates new list if heading doesn't exist.")
        void navBarModelAddHeadingCreatesAListTest() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading(TEST, DisplayRestrictions.none());
            assertNotNull(created);
        }

        @Test
        @DisplayName("Returns existing list if heading exists.")
        void navBarModelAddHeadingReturnsTheSameValuesForTheSameInputTest() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading(TEST, DisplayRestrictions.none());
            NavItemList retrieved = model.addHeading(TEST, DisplayRestrictions.none());
            assertEquals(created, retrieved);
        }

        @Test
        @DisplayName("Doesn't just always return the same list.")
        void navBarModelAddHeadingReturnsDifferentValuesForDifferentInputsTest() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading(TEST, DisplayRestrictions.none());
            NavItemList retrieved = model
                    .addHeading(TEST_B, EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertNotEquals(created, retrieved);
        }

        @Test
        @DisplayName("Add Heading by arbitrary Display Restrictions is equivilant to get by set.")
        void addHeadingWithArrayDisplayRestrictionsReturnsTheSameAsAddHeadingBySet() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList createdWithSet = model.addHeading(HEADING, DisplayRestrictions.none());
            NavItemList retrievedByArray = model.addHeading(HEADING);
            assertEquals(createdWithSet, retrievedByArray);

            NavItemList createdByArray = model
                    .addHeading(HEADING, DisplayRestrictions.USER_REQUIRED);
            NavItemList retrievedWithSet = model
                    .addHeading(HEADING, EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            assertEquals(createdByArray, retrievedWithSet);
        }

        @Test
        @DisplayName("Add Heading with no Display args, creates List with no default security.")
        void addHeadingWithArrayDisplayRestrictionsCreatesNoDefaultSecurity() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList noRequirements = model.addHeading(HEADING);
            NavBarItem child = noRequirements.add(HEADING, URL);

            assertEquals(DisplayRestrictions.none(), child.getRestrictions());
        }

        @Test
        @DisplayName("Add Heading with Display args, creates List with correct default security.")
        void addHeadingWithArrayDisplayRestrictionsCreatesCorrectDefaultSecurity() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList noRequirements = model
                    .addHeading(HEADING, DisplayRestrictions.USER_REQUIRED);
            NavBarItem child = noRequirements.add(HEADING, URL);

            assertEquals(EnumSet.of(DisplayRestrictions.USER_REQUIRED), child.getRestrictions());
        }
    }


    @Nested
    @DisplayName("Get Flag Tripped Tests:")
    class currentRestrictionTests {

        @Test
        @DisplayName("Returns None if no user.")
        void noCurrentFlagsTrippedForLoggedOutUserTest() {
            when(mockModel.getAttribute(ModelAttributeNames.USER_MODEL)).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            EnumSet<DisplayRestrictions> returned = navModelBuilder
                    .getCurrentRestrictions(mockModel);

            assertThat(returned, hasSize(0));
        }

        @Test
        @DisplayName("Returns None if null user.")
        void noCurrentFlagsTrippedForNullUserTest() {
            when(mockModel.getAttribute(ModelAttributeNames.USER_MODEL)).thenReturn(null);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            EnumSet<DisplayRestrictions> returned = navModelBuilder
                    .getCurrentRestrictions(mockModel);

            assertThat(returned, hasSize(0));
        }

        @Test
        @DisplayName("Returns None and User Required if a user is logged in.")
        void userFlagTrippedTest() {
            when(mockModel.getAttribute(ModelAttributeNames.USER_MODEL)).thenReturn(mockUser);
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

        static final String ALL_PERMISSIONS = "AllPermissions";
        static final String USER_ONLY = "UserOnly";
        static final String NO_USER = "no user";
        static final String USER = "user";

        @Test
        @DisplayName("Does Builder create navigation bar.")
        void createNavBar() {
            when(mockModel.getAttribute(ModelAttributeNames.USER_MODEL)).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();
            NavItemList userOnly = navModelBuilder
                    .addHeading(USER_ONLY, EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            userOnly.add(TEST, URL);

            NavItemList allPermissions = navModelBuilder
                    .addHeading(ALL_PERMISSIONS, DisplayRestrictions.none());
            allPermissions.add(TEST, URL);

            NavBarModel navModel = navModelBuilder.build(mockModel);
            Map<String, NavItemList> sections = navModel.getSections();

            assertThat(sections.keySet(), hasSize(1));
            assertThat(sections.keySet(), contains(ALL_PERMISSIONS));
        }

        @Test
        @DisplayName("Does not return sub menu items that aren't drawable")
        void filterSubMenuTest() {
            when(mockModel.getAttribute(ModelAttributeNames.USER_MODEL)).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();

            NavItemList allPermissions = navModelBuilder
                    .addHeading(ALL_PERMISSIONS, DisplayRestrictions.none());
            allPermissions.add(NO_USER, URL);
            allPermissions.add(USER, URL).requireLoggedInUser();

            NavBarModel navModel = navModelBuilder.build(mockModel);
            Map<String, NavItemList> sections = navModel.getSections();

            assertThat(sections.keySet(), hasSize(1));
            assertThat(sections.keySet(), contains(ALL_PERMISSIONS));

            Iterator<INavBarItem> iter = sections.get(ALL_PERMISSIONS).iterator();

            ArrayList<INavBarItem> subItems = new ArrayList<>();
            assertTrue(iter.hasNext());//Has at least one value
            while (iter.hasNext()) {
                subItems.add(iter.next());
            }
            for (final INavBarItem item : subItems) {
                assertFalse(item.getRestrictions().contains(DisplayRestrictions.USER_REQUIRED));
            }
            assertThat(subItems, hasSize(1));
            assertEquals(NO_USER, subItems.get(0).getHeading());
        }


        @Test
        @DisplayName("Simulated real case for logged out user.")
        void simulatedRealCaseForLoggedOutTest()
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
        void simulatedRealCaseForLoggedInTest() {

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
            modelMap.addAttribute(ModelAttributeNames.USER_MODEL, ret);
            return modelMap;
        }
    }

    @Nested
    class cloningTests {

        @Test
        @DisplayName("Clone Item returns null for not visible item.")
        void cloneItemReturnsNullIfItemIsntVisibleTest() {
            NavBarItem navBarItem = new NavBarItem(HEADING, URL,
                    EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, DisplayRestrictions.none());
            assertNull(clonedItem);
        }

        @Test
        @DisplayName("Clone Item returns new item with correct heading and url for visible item.")
        void cloneItemReturnsMatchingItemIfItemIsntVisibleTest() {
            NavBarItem navBarItem = new NavBarItem(HEADING, URL,
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
        void cloneItemReturnsMatchingItemWithChildrenIfChildrenAreVisibleTest() {
            NavBarItem navBarItem = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            NavBarItem child = navBarItem.add(CHILD_HEADING, CHILD_URL);
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
        void cloneItemReturnsMatchingItemWithoutChildrenIfChildrenArentVisibleTest() {
            NavBarItem navBarItem = new NavBarItem(HEADING, URL, DisplayRestrictions.none());
            NavBarItem child = navBarItem.add(CHILD_HEADING, CHILD_URL);
            child.add(CHILD_HEADING, CHILD_URL);
            child.requireLoggedInUser();
            NavBarModelBuilder builder = new NavBarModelBuilder();
            INavBarItem clonedItem = builder
                    .cloneItemIfVisible(navBarItem, DisplayRestrictions.none());
            assertThat(clonedItem.getChildren(), hasSize(0));
        }

        @Test
        @DisplayName("Clone List returns null if no children")
        void cloneListReturnsNullIfNoChildrenTest() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarModelBuilder builder = new NavBarModelBuilder();
            NavItemList clonedList = builder.cloneListIfVisible(list, DisplayRestrictions.none());
            assertNull(clonedList);
        }

        @Test
        @DisplayName("Clone List returns a new List with cloned children")
        void cloneListReturnsNewListIfChildrenAreVisibleTest() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarItem item = list.add(HEADING, URL);
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
        void cloneListReturnsNullIfNoChildrenAreVisibleTest() {
            NavItemList list = new NavItemList(DisplayRestrictions.none());
            NavBarItem item = list.add(HEADING, URL);
            item.requireLoggedInUser();
            NavBarModelBuilder builder = new NavBarModelBuilder();
            NavItemList clonedList = builder.cloneListIfVisible(list, DisplayRestrictions.none());
            assertNull(clonedList);
        }
    }
}