package uk.gov.ch.developer.docs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.ch.developer.docs.models.NavBarModel;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
public abstract class BaseController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(BaseController.class.getName());

    protected static final String ERROR_VIEW = "error";

    @Autowired
    private NavBarModel navbar;

    protected BaseController() {

    }

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    @ModelAttribute("templateTitle")
    public abstract String getTemplateTitle();

    @ModelAttribute("navBarModel")
    public NavBarModel getNavBar() {
        return navbar;
    }

}
