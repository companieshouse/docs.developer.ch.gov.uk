package uk.gov.ch.developer.docs.controller.developer;

import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.ch.oauth.exceptions.UnauthorisedException;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController {

    @Autowired
    private IOAuthCoordinator coordinator;

    @GetMapping
    public String callback(
            @RequestParam final Map<String, String> allParams,
            final HttpServletResponse response
    ) {
        try {
            return String.format("redirect:%s",
                    coordinator.getPostCallbackRedirectURL(response, allParams)
            );
        } catch (UnauthorisedException e) {
            return "error";
        }
    }
}