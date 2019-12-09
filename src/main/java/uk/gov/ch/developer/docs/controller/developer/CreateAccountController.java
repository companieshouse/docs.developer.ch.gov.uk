package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("${createAccount.url}")
public class CreateAccountController extends BaseController {

    private static final String TITLE = "How to Set Up a Companies House Account";
    @Value("${createAccount.path}")
    private String path;

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return path;
    }

    @Override
    public String getTemplateTitle() {
        return TITLE;
    }

}
