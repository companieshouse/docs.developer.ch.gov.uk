package uk.gov.ch.developer.docs.interceptor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.LogContextProperties;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {

    @Mock
    HttpServletRequest mockRequest;
    @Mock
    HttpServletResponse mockResponse;
    @Mock
    Object mockHandler;
    @Mock
    ModelAndView mockModelAndView;
    @Mock
    HttpSession mockSession;

    @Mock
    private Logger logger;

    @InjectMocks
    LoggingInterceptor interceptor;

    @Test
    @DisplayName("Tests the interceptor invokes the Request Logger to pre handle the start of the request")
    void test_preHandle_Invokes_RequestLogger() {
        ReflectionTestUtils.setField(interceptor, "logger", logger);
        when(mockRequest.getSession()).thenReturn(mockSession);
        assertTrue(interceptor.preHandle(mockRequest, mockResponse, mockHandler));
        verify(logger, times(1)).infoStartOfRequest(any());
    }

    @Test
    @DisplayName("Tests the interceptor invokes the Request Logger to post handle the end of the request")
    void test_postHandle_Invokes_RequestLogger() {
        ReflectionTestUtils.setField(interceptor, "logger", logger);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getAttribute(LogContextProperties.START_TIME_KEY.value())).thenReturn(System.currentTimeMillis());
        when(mockResponse.getStatus()).thenReturn(200);
        interceptor.postHandle(mockRequest, mockResponse, mockHandler, mockModelAndView);
        verify(logger, times(1)).infoEndOfRequest(any(), eq(200), anyLong());
    }

    @Test
    void test_constructor_doesnt_Error() {
        LoggingInterceptor newInterceptor = new LoggingInterceptor();
        assertNotNull(newInterceptor);
    }
}