package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${createAccount.url}")
public class CreateAccountController extends AbstractPageController {

    private static final String TITLE = "How to Create a Companies House Account";

    @Value("${createAccount.path}")
    private String path;

    public CreateAccountController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }
}
