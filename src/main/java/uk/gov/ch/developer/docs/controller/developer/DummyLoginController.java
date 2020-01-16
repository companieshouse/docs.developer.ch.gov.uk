package uk.gov.ch.developer.docs.controller.developer;

import java.lang.reflect.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.ch.developer.docs.models.nav.NavBarModelBuilder;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

@Controller
public class DummyLoginController {

    @Autowired
    private NavBarModelBuilder navbarFactory;

    @RequestMapping({"forceLogin", "signin"})
    public ModelAndView forceLogin(ModelAndView m)
            throws NoSuchFieldException, IllegalAccessException {
        final UserModel user = new UserModel();

        final UserProfile userProfile = new UserProfile();
        userProfile.setEmail("Forced@login.gmail.com");

        final SignInInfo signInInfo = new SignInInfo();
        signInInfo.setSignedIn(true);
        signInInfo.setUserProfile(userProfile);

        Field siField = user.getClass().getDeclaredField("signIn");
        siField.setAccessible(true);
        siField.set(user, signInInfo);

        m.getModel().put("user", user);
        m.getModel().put("navBarModel", navbarFactory.build(m.getModelMap()));
        m.setViewName("dev-hub/home");
        return m;
    }

    @RequestMapping({"forceLogout", "signout"})
    public ModelAndView forceLogOut(ModelAndView m) {
        final UserModel user = new UserModel();

        m.getModel().put("user", user);
        m.getModel().put("navBarModel", navbarFactory.build(m.getModelMap()));
        m.setViewName("dev-hub/home");
        return m;
    }
}
