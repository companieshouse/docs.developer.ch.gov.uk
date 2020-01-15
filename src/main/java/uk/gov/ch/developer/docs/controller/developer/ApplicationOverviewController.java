package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${manageApplications.url}")
public class ApplicationOverviewController extends AbstractPageController {

    private static final String TITLE = "Application Overview";

    @Value("${manageApplications.path}")
    private String path;

    public ApplicationOverviewController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }

}
