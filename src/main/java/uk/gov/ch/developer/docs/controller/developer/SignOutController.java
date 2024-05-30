package uk.gov.ch.developer.docs.controller.developer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.handler.SessionHandler;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uk.gov.companieshouse.session.handler.SessionHandler.CHS_SESSION_REQUEST_ATT_KEY;

@Controller
public class SignOutController {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);



    @GetMapping("${signout.url}")
    public void doSignOut(final HttpServletResponse httpServletResponse,
                          final HttpServletRequest httpServletRequest) throws IOException {

        LOGGER.debug("Processing sign out");

        Session session = (Session) httpServletRequest.getAttribute(CHS_SESSION_REQUEST_ATT_KEY);
        httpServletResponse.sendRedirect(session.signOut(httpServletRequest));
    }

}