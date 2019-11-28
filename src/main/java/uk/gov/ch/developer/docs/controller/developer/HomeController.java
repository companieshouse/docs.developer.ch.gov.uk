package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

    private static final String DOCS_HOME = "docs/home";

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return DOCS_HOME;
    }
}