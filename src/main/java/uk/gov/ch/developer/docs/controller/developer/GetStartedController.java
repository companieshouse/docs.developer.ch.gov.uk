package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${getStarted.url}")
public class GetStartedController extends AbstractPageController {

    private static final String TITLE = "Get started with the Companies House API";

    @Value("${getStarted.path}")
    private String path;

    public GetStartedController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }

}
