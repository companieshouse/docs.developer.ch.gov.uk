package uk.gov.ch.developer.docs.controller.developer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.controller.BaseController;


@Controller
@RequestMapping("${howToObtainAPIKey.url}")
public class HowToObtainAPIKeyController extends BaseController {

    private static final String TITLE = "How to Obtain an API Key";
    @Value("${howToObtainAPIKey.path}")
    private String path;

    @GetMapping
    public String get() {
        return getTemplateName();
    }

    @Override
    protected String getTemplateName() {
        return path;
    }

    @Override
    public String getTemplateTitle() {
        return TITLE;
    }
}
