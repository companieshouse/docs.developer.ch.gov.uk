package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("/dev-hub/create-account")
public class CreateAccountController extends BaseController {

    private static final String DEV_HUB_CREATE_ACCOUNT = "dev-hub/createAccount";
    private static final String TEMPLATE_TITLE = "Set up a Companies House account";

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return DEV_HUB_CREATE_ACCOUNT;
    }

    @Override
    public String getTemplateTitle() {
        return TEMPLATE_TITLE;
    }

}
