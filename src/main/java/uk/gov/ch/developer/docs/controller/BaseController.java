package uk.gov.ch.developer.docs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.ch.developer.docs.DocsWebApplication.APPLICATION_NAME_SPACE;

@Controller
public abstract class BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @ModelAttribute("templateName")
    protected abstract String getTemplateName();

    @ModelAttribute("templateTitle")
    public abstract String getTemplateTitle();

    @ModelAttribute("isErrorPage")
    public boolean isErrorPage() {
        return false;
    }

}
