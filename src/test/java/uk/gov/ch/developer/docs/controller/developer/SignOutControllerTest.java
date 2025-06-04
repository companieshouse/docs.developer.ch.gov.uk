package uk.gov.ch.developer.docs.controller.developer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignOutControllerTest {

    @Mock
    private HttpServletResponse httpServletResponse;
    @Spy
    private Logger logger = LoggerFactory.getLogger(DocsWebApplication.APPLICATION_NAME_SPACE);

    private final SignOutController signOutController = new SignOutController(logger);

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    void testDoSignOut() throws IOException {
        ReflectionTestUtils.setField(signOutController, "redirectUri", "/dev-hub");
        signOutController.doSignOut(httpServletResponse);

        verify(httpServletResponse).sendRedirect("/dev-hub");
    }
}