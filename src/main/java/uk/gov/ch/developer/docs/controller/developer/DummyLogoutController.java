package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.models.user.IUserModel;
import uk.gov.ch.developer.docs.models.user.UserModel;

@Controller
@RequestMapping(value = {"forceLogout", "signout"})
public class DummyLogoutController extends HomeController {

    @Override
    public IUserModel getUser() {
        UserModel user = new UserModel();
        return user;
    }

}
