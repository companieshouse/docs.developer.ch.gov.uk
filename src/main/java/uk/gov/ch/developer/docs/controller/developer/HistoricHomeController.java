package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${historicHome.url}")
public class HistoricHomeController {

    @Value("${absoluteHome.url}")
    private String redirectUriPage;

    @GetMapping
    public String getPath() {
        return String.format("redirect:%s", redirectUriPage);
    }
}
