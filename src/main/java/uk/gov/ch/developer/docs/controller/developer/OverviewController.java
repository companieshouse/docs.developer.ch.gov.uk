package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${overview.url}")
public class OverviewController extends AbstractPageController {

    private static final String TITLE = "Companies House API Overview";

    @Value("${overview.path}")
    private String path;

    public OverviewController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }
}