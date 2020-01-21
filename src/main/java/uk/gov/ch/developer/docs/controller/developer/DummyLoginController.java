package uk.gov.ch.developer.docs.controller.developer;

import java.lang.reflect.Field;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;
import uk.gov.companieshouse.session.model.SignInInfo;
import uk.gov.companieshouse.session.model.UserProfile;

/**
 * This is a temporary controller to assist in testing, intended to be deleted after login and
 * logout are implemented correctly.
 */
@Controller
@RequestMapping(value = {"forceLogin", "signin"})
public class DummyLoginController extends HomeController {

    @Override
    public IUserModel getUser() {
        UserModel user = new UserModel();
        try {
            final UserProfile userProfile = new UserProfile();
            userProfile.setEmail("Forced@login.companieshouse.gov.uk");

            final SignInInfo signInInfo = new SignInInfo();
            signInInfo.setSignedIn(true);
            signInInfo.setUserProfile(userProfile);


            final Field siField = user.getClass().getDeclaredField("signIn");
            //Sonar is understandably concerned about reflective coding however as this is temporary
            // and static we are not concerned that this represents a security vulnerability.
            siField.setAccessible(true);//NOSONAR
            siField.set(user, signInInfo);//NOSONAR
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return user;
    }
}
