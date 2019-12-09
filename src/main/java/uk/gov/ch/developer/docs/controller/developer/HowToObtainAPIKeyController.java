package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${howToObtainAPIKey.url}")
public class HowToObtainAPIKeyController extends AbstractPageController {

    private static final String TITLE = "How to Obtain an API Key";

    @Value("${howToObtainAPIKey.path}")
    private String path;


    public HowToObtainAPIKeyController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }
}
