package uk.gov.ch.developer.docs.controller.developer;

import java.lang.reflect.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.ch.developer.docs.models.nav.NavBarModelBuilder;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

/**
 * This is a temporary controller to assist in testing, intended to be deleted after login and
 * logout are implemented correctly.
 */
@Controller
public class DummyLoginController {

    @Autowired
    private NavBarModelBuilder navbarFactory;

    @RequestMapping(value = {"forceLogin", "signin"}, method = RequestMethod.GET)
    public ModelAndView forceLogin(final ModelAndView modelAndView)
            throws NoSuchFieldException, IllegalAccessException {
        final UserModel user = new UserModel();

        final UserProfile userProfile = new UserProfile();
        userProfile.setEmail("Forced@login.companieshouse.gov.uk");

        final SignInInfo signInInfo = new SignInInfo();
        signInInfo.setSignedIn(true);
        signInInfo.setUserProfile(userProfile);

        Field siField = user.getClass().getDeclaredField("signIn");
        //Sonar is understandably concerned about reflective coding however as this is temporary
        // and static we are not concerned that this represents a security vulnerability.
        siField.setAccessible(true);//NOSONAR
        siField.set(user, signInInfo);//NOSONAR

        modelAndView.getModel().put("user", user);
        modelAndView.getModel().put("navBarModel", navbarFactory.build(modelAndView.getModelMap()));
        modelAndView.setViewName("dev-hub/home");
        return modelAndView;
    }

    @RequestMapping(value = {"forceLogout", "signout"}, method = RequestMethod.GET)
    public ModelAndView forceLogOut(final ModelAndView modelAndView) {
        final UserModel user = new UserModel();

        modelAndView.getModel().put("user", user);
        modelAndView.getModel().put("navBarModel", navbarFactory.build(modelAndView.getModelMap()));
        modelAndView.setViewName("dev-hub/home");
        return modelAndView;
    }
}
