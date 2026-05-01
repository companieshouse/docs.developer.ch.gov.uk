package uk.gov.ch.developer.docs.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Value("${signin.url}")
    private String signinUrl;

    @Value("${developerforum.url}")
    private String developerforumUrl;

    @ModelAttribute("signinUrl")
    public String signinUrl() {
        return signinUrl;
    }

    @ModelAttribute("developerforumUrl")
    public String developerforumUrl() {
        return developerforumUrl;
    }
}
