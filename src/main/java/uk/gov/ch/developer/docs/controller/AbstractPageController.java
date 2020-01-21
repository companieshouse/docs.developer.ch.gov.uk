package uk.gov.ch.developer.docs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.ch.developer.docs.models.nav.NavBarModel;
import uk.gov.ch.developer.docs.models.nav.NavBarModelBuilder;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.ch.developer.docs.session.SessionService;

public abstract class AbstractPageController extends BaseController {

    private final String title;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private NavBarModelBuilder navbarFactory;

    public AbstractPageController(final String title) {
        this.title = title;
    }

    public abstract String getPath();

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return getPath();
    }

    @Override
    public String getTemplateTitle() {
        return title;
    }

    @ModelAttribute(ModelAttributeNames.USER_MODEL)
    public IUserModel getUser() {
        UserModel user = new UserModel();
        user.populateUserDetails(sessionService.getSessionFromContext());
        return user;
    }

    /**
     * @param iUserModel This injection ensures that the user model is built before the {@link
     * NavBarModel} which depends on it.
     * @return A {@link NavBarModel} which is sensitive to the login state of a user.
     */
    @ModelAttribute(ModelAttributeNames.NAV_BAR_MODEL)
    public NavBarModel getNavBar(ModelMap model,
            @ModelAttribute(ModelAttributeNames.USER_MODEL) IUserModel iUserModel) {
        return navbarFactory.build(model);
    }
}
