package uk.gov.ch.developer.docs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Controller
public abstract class BaseController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(BaseController.class.getName());

    protected static final String ERROR_VIEW = "error";


    protected BaseController() {

    }

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    @ModelAttribute("templateTitle")
    public abstract String getTemplateTitle();

}
