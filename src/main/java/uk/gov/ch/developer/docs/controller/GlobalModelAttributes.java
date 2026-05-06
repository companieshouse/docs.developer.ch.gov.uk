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

    @Value("${signout.url}")
    private String signoutUrl;

    @Value("${chs.url}")
    private String chsUrl;

    @Value("${home.url}")
    private String homeUrl;

    @Value("${cdn.url}")
    private String cdnUrl;

    @Value("${getStarted.url}")
    private String getStartedUrl;

    @Value("${developerSpecs.url}")
    private String developerSpecsUrl;

    @Value("${piwik.url}")
    private String piwikUrl;

    @Value("${piwik.siteId}")
    private String piwikSiteId;

    @Value("${authentication.url}")
    private String authenticationUrl;

    @Value("${overview.url}")
    private String overviewUrl;

    @Value("${howToCreateApplication.url}")
    private String howToCreateApplicationUrl;

    @Value("${developerGuidelines.url}")
    private String developerGuidelinesUrl;

    @ModelAttribute("signinUrl")
    public String signinUrl() {
        return signinUrl;
    }

    @ModelAttribute("developerforumUrl")
    public String developerforumUrl() {
        return developerforumUrl;
    }

    @ModelAttribute("signoutUrl")
    public String signoutUrl() {
        return signoutUrl;
    }

    @ModelAttribute("chsUrl")
    public String chsUrl() {
        return chsUrl;
    }

    @ModelAttribute("homeUrl")
    public String homeUrl() {
        return homeUrl;
    }

    @ModelAttribute("cdnUrl")
    public String cdnUrl() {
        return cdnUrl;
    }

    @ModelAttribute("getStartedUrl")
    public String getStartedUrl() {
        return getStartedUrl;
    }

    @ModelAttribute("piwikUrl")
    public String piwikUrl() {
        return piwikUrl;
    }

    @ModelAttribute("piwikSiteId")
    public String piwikSiteId() {
        return piwikSiteId;
    }

    @ModelAttribute("authenticationUrl")
    public String authenticationUrl() {
        return authenticationUrl;
    }

    @ModelAttribute("developerSpecsUrl")
    public String developerSpecsUrl() {
        return developerSpecsUrl;
    }

    @ModelAttribute("overviewUrl")
    public String overviewUrl() {
        return overviewUrl;
    }

    @ModelAttribute("howToCreateApplicationUrl")
    public String howToCreateApplicationUrl() {
        return howToCreateApplicationUrl;
    }

    @ModelAttribute("developerGuidelinesUrl")
    public String developerGuidelinesUrl() {
        return developerGuidelinesUrl;
    }
}
