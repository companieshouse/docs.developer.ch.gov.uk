package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.oauth.IOAuthCoordinator;

@Controller
@RequestMapping("${signin.url}")
public class SignInController {


    @Autowired
    private IOAuthCoordinator coordinator;


    @GetMapping
    public void doSignIn(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse) throws IOException {
        final String authoriseUri = coordinator.getAuthoriseUriFromRequest(httpServletRequest);
        httpServletResponse.sendRedirect(authoriseUri);
    }
}