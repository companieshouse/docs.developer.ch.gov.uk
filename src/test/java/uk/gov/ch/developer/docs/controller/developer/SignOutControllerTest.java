package uk.gov.ch.developer.docs.controller.developer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignOutControllerTest {

    @Mock
    private HttpServletResponse httpServletResponse;

    private final SignOutController signOutController = new SignOutController();

    @Test
    @DisplayName("Test that a user is redirected upon signing out")
    void testDoSignOut() throws IOException {
        ReflectionTestUtils.setField(signOutController, "redirectUri", "/dev-hub");
        signOutController.doSignOut(httpServletResponse);

        verify(httpServletResponse).sendRedirect("/dev-hub");
    }
}