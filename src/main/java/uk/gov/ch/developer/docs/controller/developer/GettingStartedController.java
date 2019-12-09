package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${gettingStarted.url}")
public class GettingStartedController extends AbstractPageController {

    private static final String TITLE = "Getting started with the Companies House API";

    @Value("${gettingStarted.path}")
    private String path;

    public GettingStartedController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }

}
