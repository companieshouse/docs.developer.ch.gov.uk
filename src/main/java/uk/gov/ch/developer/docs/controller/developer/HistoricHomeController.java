package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.ch.developer.docs.DocsWebApplication.APPLICATION_NAME_SPACE;

@Controller
@RequestMapping("${historicHome.url}")
public class HistoricHomeController {

    private final Logger logger = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Value("${absoluteHome.url}")
    private String redirectUriPage;

    @GetMapping
    public String getPath() {
        logger.trace(String.format("HomeController redirecting to %s", redirectUriPage));
        return String.format("redirect:%s", redirectUriPage);
    }
}
