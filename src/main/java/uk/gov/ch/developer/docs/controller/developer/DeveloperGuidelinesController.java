package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${developerGuidelines.url}")
public class DeveloperGuidelinesController extends AbstractPageController {

    private static final String TITLE = "Developer Guidelines";

    @Value("${developerGuidelines.path}")
    private String path;


    public DeveloperGuidelinesController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }
}
