package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.AbstractPageController;

@Controller
@RequestMapping("${authentication.url}")
public class AuthenticationController extends AbstractPageController {

    private static final String TITLE = "Authentication";
    
    @Value("${authentication.path}")
    private String path;

    public AuthenticationController() {
        super(TITLE);
    }

    @Override
    public String getPath() {
        return path;
    }

}
