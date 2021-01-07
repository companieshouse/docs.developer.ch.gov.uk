package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${historicHome.url}")
public class HistoricHomeController extends AbstractPageController {

    private static final String TITLE = "Developer Hub Home";

    @Value("${absoluteHome.url}")
    private String redirectUriPage;

    public HistoricHomeController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return String.format("redirect:%s", redirectUriPage);
    }
}
