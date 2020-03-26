package uk.gov.ch.developer.docs.controller.developer;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.ch.oauth.IIdentityProvider;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;
import uk.gov.companieshouse.session.model.SignInInfo;

@Controller
@RequestMapping("${signout.url}")
public class SignOutController {
    private static final Logger LOGGER = LoggerFactory.getLogger("docs.developer.ch.gov.uk");

    @Autowired
    private SessionService sessionService;
    @Autowired
    private IIdentityProvider identityProvider;

    @GetMapping
    public void doSignOut(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {

        String zxsKey = ".zxs_key";

        final Session session = (Session) httpServletRequest
                .getAttribute(SessionHandler.CHS_SESSION_REQUEST_ATT_KEY);

        SignInInfo signInInfo = session.getSignInInfo();

        if (signInInfo.isSignedIn()) {
            Map<String, Object> sData = session.getData();

            Map<String, Object> signedInInfo =
                    (Map<String, Object>) sData.get(SessionKeys.SIGN_IN_INFO.getKey());

            signedInInfo.put(SessionKeys.SIGNED_IN.getKey(), 0);
            sData.remove(SessionKeys.SIGN_IN_INFO.getKey());
            sData.remove(zxsKey);

        }

        httpServletResponse.sendRedirect(identityProvider.getRedirectUriPage());
    }

}
