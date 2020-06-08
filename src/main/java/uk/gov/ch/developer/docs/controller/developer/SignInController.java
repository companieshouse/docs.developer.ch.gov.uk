package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.oauth.IOauth;
import uk.gov.ch.oauth.identity.IIdentityProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("${signin.url}")
public class SignInController {


    @Autowired
    private IOauth oauth2;

    @Autowired
    IIdentityProvider identityProvider;


    @GetMapping
    public void doSignIn(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse) throws IOException {
        final String state = oauth2.prepareState(httpServletRequest);
        final String authoriseUri = identityProvider.getAuthorisationUrl(state);
        httpServletResponse.sendRedirect(authoriseUri);
    }
}
