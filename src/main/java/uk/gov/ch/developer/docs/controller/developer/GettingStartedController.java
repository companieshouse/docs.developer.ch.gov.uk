package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;

@Controller
@RequestMapping("/dev-hub/getting-started")
public class GettingStartedController extends BaseController {

    private static final String DEV_HUB_GETTING_STARTED = "dev-hub/gettingStarted";
    private static final String TEMPLATE_TITLE = "Getting started with the Companies House API";

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {

        return DEV_HUB_GETTING_STARTED;
    }

    @Override
    public String getTemplateTitle() {
        return TEMPLATE_TITLE; }

}
