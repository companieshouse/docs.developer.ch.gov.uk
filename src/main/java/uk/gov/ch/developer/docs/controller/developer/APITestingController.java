package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${apiTesting.url}")
public class APITestingController extends AbstractPageController {

    private static final String TITLE = "API testing";

    @Value("${apiTesting.path}")
    private String path;


    public APITestingController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }
}
