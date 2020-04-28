package uk.gov.ch.developer.docs.controller.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultErrorControllerTest {

    //MockMVC testing is not configured for ErrorController classes

    private static final String NOT_FOUND_VIEW = "dev-hub/error/404error";
    private static final String SERVER_ERROR_VIEW = "dev-hub/error/serviceError";
    private static final String DEFAULT_ERROR_VIEW = "error";

    @Mock
    HttpServletRequest mockRequest;
    @InjectMocks
    @Spy
    private DefaultErrorController controller;

    @Test
    @DisplayName("404 Error")
    void test_GetInvalidURL_ReturnsView() {
        when(mockRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(404);
        when(controller.getNotFoundPath()).thenReturn("dev-hub/error/404error");

        String view = controller.get();

        assertEquals(NOT_FOUND_VIEW, view);
    }

    @Test
    @DisplayName("Server Error")
    void test_InternalServerError_ReturnsView() {
        when(mockRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(500);
        when(controller.getServiceErrorPath()).thenReturn("dev-hub/error/serviceError");

        String view = controller.get();

        assertEquals(SERVER_ERROR_VIEW, view);
    }

    @Test
    @DisplayName("Default Error")
    void test_Default_ReturnsView() {
        when(mockRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)).thenReturn(-1);

        String view = controller.get();

        assertEquals(DEFAULT_ERROR_VIEW, view);
    }
}