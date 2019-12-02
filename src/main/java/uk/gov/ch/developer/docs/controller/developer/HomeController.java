package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("/dev-hub")
public class HomeController extends BaseController {

    private static final String DEV_HUB = "dev-hub/home";
    private static final String TEMPLATE_TITLE = "Developer Hub Home";

    @GetMapping
    public String get() {

        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {

        return DEV_HUB;
    }

    @Override
    public String getTemplateTitle() {
        return TEMPLATE_TITLE; }
}