package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("/dev-hub/gettingstarted")
public class GettingStartedController extends BaseController {

    public static final String TEMPLATE_NAME = "docs/gettingStarted";
    private static final String TEMPLATE_TITLE = "Getting started with the Companies House API";

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return TEMPLATE_NAME;
    }


    @Override
    public String getTemplateTitle() {
        return TEMPLATE_TITLE;
    }
}