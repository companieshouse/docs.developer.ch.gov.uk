package uk.gov.ch.developer.docs.models.nav;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;

/**
 * Iterable to group related INavBarItems.
 */
public class NavItemList implements Iterable<INavBarItem> {

    private boolean defaultRequiresLoggedIn;
    private List<INavBarItem> list = new ArrayList<>();

    NavItemList(boolean defaultRequiresLoggedIn) {
        this.defaultRequiresLoggedIn = defaultRequiresLoggedIn;
    }

    /**
     * Adds a new child to this item to be displayed as a sub item in the menu. The child inherits
     * the required signin of this list.
     *
     * @param heading label of the sub menu item.
     * @param url link address of the sub menu item.
     * @return the newly created child item.
     */
    public NavBarItem add(String heading, String url) {
        NavBarItem newItem = new NavBarItem(heading, url, defaultRequiresLoggedIn);
        list.add(newItem);
        return newItem;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<INavBarItem> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    /**
     * Thymeleaf method used to prevent empty section headers being drawn. This only checks the
     * highest level as its assumed that restrictions on parents will apply to children.
     *
     * @param userModel model of the logged in user model.
     * @return <code>true</code> if the sub items of this list contains at least one item that
     * should be drawn. Otherwise returns <code>false</code>.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean hasDrawableChildren(IUserModel userModel) {
        return list.stream()
                .anyMatch(item -> UserModel.isUserSignedIn(userModel) || !item.isLoggedInOnly());
    }
}
