package uk.gov.ch.developer.docs.controller.developer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignOutControllerTest {

    private static final String REDIRECT_PAGE = "/home";

    @Mock
    private HttpServletResponse httpServletResponse;
    @Spy
    private Logger logger = LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    private final SignOutController signOutController = new SignOutController(logger, REDIRECT_PAGE);

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    void testDoSignOut() throws IOException {
        signOutController.doSignOut(httpServletResponse);

        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }
}