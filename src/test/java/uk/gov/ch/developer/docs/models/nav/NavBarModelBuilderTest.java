package uk.gov.ch.developer.docs.models.nav;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
            NavItemList created = model.addHeading("Test", DisplayRestrictions.NONE());
            assertNotNull(created);
        }

        @Test
        @DisplayName("Returns existing list if heading exists.")
        void NavBarModel_addHeading_ReturnsTheSameValues_ForTheSameInput_test() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading("Test", DisplayRestrictions.NONE());
            NavItemList retrieved = model.addHeading("Test", DisplayRestrictions.NONE());
            assertEquals(created, retrieved);
        }

        @Test
        @DisplayName("Doesn't just always return the same list.")
        void NavBarModel_addHeading_ReturnsDifferentValues_ForDifferentInputs_test() {
            NavBarModelBuilder model = new NavBarModelBuilder();
            NavItemList created = model.addHeading("Test", DisplayRestrictions.NONE());
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
    class endToEndTests {

        @Test
        void createNavBar() {
            when(mockModel.getAttribute("user")).thenReturn(mockUser);
            when(mockUser.isSignedIn()).thenReturn(false);

            NavBarModelBuilder navModelBuilder = new NavBarModelBuilder();
            NavItemList userOnly = navModelBuilder
                    .addHeading("UserOnly", EnumSet.of(DisplayRestrictions.USER_REQUIRED));
            userOnly.add("test", "url");

            NavItemList allPermisions = navModelBuilder
                    .addHeading("AllPermissions", DisplayRestrictions.NONE());
            allPermisions.add("test", "url");

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
                    .addHeading("AllPermissions", DisplayRestrictions.NONE());
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

    }

    @Nested
    class cloningTests {

        @Test
        void cloneNavBarItem_ReturnsNullIfItem_IsntVisible()
                throws IllegalAccessException, NoSuchFieldException {
//            INavBarItem item = mock(INavBarItem.class);
//            when(item.isVisible(DisplayRestrictions.NONE())).thenReturn(false);

            NavBarModelBuilder builderWithMocks = new NavBarModelBuilder();

            NavItemList manageApplications = builderWithMocks
                    .addHeading("Manage Applications",
                            EnumSet.of(DisplayRestrictions.USER_REQUIRED));

            manageApplications.add("View all applications", "example.com");
            manageApplications.add("Add an application", "example.com");

            NavItemList documentation = builderWithMocks
                    .addHeading("General Documentation", DisplayRestrictions.NONE());
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
            for (Entry<String, NavItemList> entry : map.entrySet()) {
                System.out.println(entry.getKey());
            }

            System.out.println();

            ModelMap userModel = addUserToModel(new ModelMap());

            NavBarModel outWithUser = builderWithMocks.build(userModel);
            Map<String, NavItemList> map_withUser = outWithUser.getSections();
            for (Entry<String, NavItemList> entry : map_withUser.entrySet()) {
                System.out.println(entry.getKey());
            }
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
}