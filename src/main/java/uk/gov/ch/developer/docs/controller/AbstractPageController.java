package uk.gov.ch.developer.docs.controller;

import org.springframework.web.bind.annotation.GetMapping;

public abstract class AbstractPageController extends BaseController {

    private final String title;

    public AbstractPageController(final String title) {
        this.title = title;
    }

    public abstract String getPath();

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return getPath();
    }

    @Override
    public String getTemplateTitle() {
        return title;
    }
}
