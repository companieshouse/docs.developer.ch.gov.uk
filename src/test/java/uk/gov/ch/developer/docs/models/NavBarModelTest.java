package uk.gov.ch.developer.docs.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NavBarModelTest {

    @Test
    void NavBarModel_addHeading_CreatesAList_test() {
        NavBarModel model = new NavBarModel();
        NavItemList created = model.addHeading("Test");
        assertNotNull(created);
    }

    @Test
    void NavBarModel_addHeading_ReturnsTheSameValues_ForTheSameInput_test() {
        NavBarModel model = new NavBarModel();
        NavItemList created = model.addHeading("Test");
        NavItemList retrieved = model.addHeading("Test");
        assertEquals(created, retrieved);
    }

    @Test
    void NavBarModel_addHeading_ReturnsDifferentValues_ForDifferentInputs_test() {
        NavBarModel model = new NavBarModel();
        NavItemList created = model.addHeading("Test");
        NavItemList retrieved = model.addHeading("TestB");
        assertNotEquals(created, retrieved);
    }
}