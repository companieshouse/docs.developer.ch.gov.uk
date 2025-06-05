package uk.gov.ch.developer.docs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.companieshouse.logging.Logger;

@Controller
public abstract class BaseController {

    @Autowired
    protected Logger logger;

    BaseController() {

    }

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    @ModelAttribute("templateTitle")
    public abstract String getTemplateTitle();

    @ModelAttribute("isErrorPage")
    public boolean isErrorPage() {
        return false;
    }

}
