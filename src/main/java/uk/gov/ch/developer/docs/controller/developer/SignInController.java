package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.gov.ch.oauth.IOAuthCoordinator;
import uk.gov.companieshouse.logging.Logger;

@Controller
@RequestMapping("${signin.url}")
public class SignInController {


    private final IOAuthCoordinator coordinator;
    private final Logger logger;

    @Autowired
    public SignInController(IOAuthCoordinator coordinator, Logger logger) {
        this.coordinator = coordinator;
        this.logger = logger;
    }


    @GetMapping
    public void doSignIn(@RequestParam final Map<String, String> allParams,
                         final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse) throws IOException {
        final String authoriseUri = coordinator.getAuthoriseUriFromRequest(httpServletRequest, allParams);
        httpServletResponse.sendRedirect(authoriseUri);
    }
}