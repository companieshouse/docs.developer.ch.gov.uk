package uk.gov.ch.developer.docs.models.nav;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.hamcrest.collection.IsMapWithSize.anEmptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NavBarModelTest {

    @Nested
    @DisplayName("Add Heading Tests:")
    class addTests {

        @Test
        @DisplayName("Creates new list if heading doesn't exist.")
        void NavBarModel_addHeading_CreatesAList_test() {
            NavBarModel model = new NavBarModel();
            NavItemList created = model.addHeading("Test", UserRequired.USER_REQUIRED);
            assertNotNull(created);
        }

        @Test
        @DisplayName("Returns existing list if heading exists.")
        void NavBarModel_addHeading_ReturnsTheSameValues_ForTheSameInput_test() {
            NavBarModel model = new NavBarModel();
            NavItemList created = model.addHeading("Test", UserRequired.USER_REQUIRED);
            NavItemList retrieved = model.addHeading("Test", UserRequired.USER_REQUIRED);
            assertEquals(created, retrieved);
        }

        @Test
        @DisplayName("Doesn't just always return the same list.")
        void NavBarModel_addHeading_ReturnsDifferentValues_ForDifferentInputs_test() {
            NavBarModel model = new NavBarModel();
            NavItemList created = model.addHeading("Test", UserRequired.USER_REQUIRED);
            NavItemList retrieved = model.addHeading("TestB", UserRequired.USER_NOT_REQUIRED);
            assertNotEquals(created, retrieved);
        }
    }

    @Nested
    @DisplayName("Get Heading Tests:")
    class getHeadingTests {

        @Test
        @DisplayName("Returns null if header doesn't exist.")
        void NavBarModel_getHeading_returnsNull_IfHeaderDoesntExist_test() {
            NavBarModel model = new NavBarModel();
            model.addHeading("Test", UserRequired.USER_REQUIRED);
            NavItemList retrieved = model.getHeading("TestB");
            assertNull(retrieved);
        }

        @Test
        @DisplayName("Returns value if header does exist.")
        void NavBarModel_getHeading_returnsValue_IfHeaderDoesExist_test() {
            NavBarModel model = new NavBarModel();
            NavItemList created = model.addHeading("Test", UserRequired.USER_REQUIRED);
            NavItemList retrieved = model.getHeading("Test");
            assertEquals(created, retrieved);
        }
    }

    @Nested
    @DisplayName("Get Section tests:")
    class getSectionTests {

        @Test
        @DisplayName("Returns empty map when there are no sections.")
        void NavBarModel_getSections_retrieves_emptyMap_ifNoSections_test() {
            NavBarModel model = new NavBarModel();
            Map<String, NavItemList> sections = model.getSections();
            assertThat(sections, anEmptyMap());
        }

        @Test
        @DisplayName("Returns map with correct values when there are sections.")
        void NavBarModel_getSections_retrieves_aMapContainingValuesCreated_test() {
            NavBarModel model = new NavBarModel();
            NavItemList created = model.addHeading("Test", UserRequired.USER_REQUIRED);
            Map<String, NavItemList> sections = model.getSections();
            assertThat(sections, aMapWithSize(1));
            assertThat(sections, hasEntry("Test", created));
        }
    }
}