package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${home.url}")
public class HomeController extends AbstractPageController {

    private static final String TITLE = "Developer Hub Home";

    @Value("${home.path}")
    private String path;

    public HomeController() {
        super(TITLE);
        logger.trace(String.format("HomeController initialized with title: {%s}", TITLE));
    }

    @Override
    public String getPath() {
        return path;
    }
}