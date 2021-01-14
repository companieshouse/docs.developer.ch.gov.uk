package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${howToCreateApplication.url}")
public class HowToCreateApplicationController extends AbstractPageController {

    private static final String TITLE = "How to create an application";

    @Value("${howToCreateApplication.path}")
    private String path;

    public HowToCreateApplicationController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }

}
