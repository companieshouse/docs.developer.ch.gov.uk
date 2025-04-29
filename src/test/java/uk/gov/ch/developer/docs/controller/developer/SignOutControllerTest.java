package uk.gov.ch.developer.docs.controller.developer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignOutControllerTest {

    private static final String REDIRECT_PAGE = "/home";

    @Mock
    private HttpServletResponse httpServletResponse;

    private final SignOutController signOutController = new SignOutController(REDIRECT_PAGE);

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    void testDoSignOut() throws IOException {
        signOutController.doSignOut(httpServletResponse);

        verify(httpServletResponse).sendRedirect(REDIRECT_PAGE);
    }
}