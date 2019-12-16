package uk.gov.ch.developer.docs.interceptor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.RequestLogger;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggingInterceptorTest {

    @Mock
    RequestLogger mockRequestLogger;
    @Mock
    HttpServletRequest mockRequest;
    @Mock
    HttpServletResponse mockResponse;
    @Mock
    Object mockHandler;
    /**
     * This is being done using mock() rather than through the annotation so that it is initalised
     * before @InjectMocks
     */
    private Logger mockLogger = mock(Logger.class);
    @Mock
    ModelAndView mockModelAndView;

    /**
     * This is initalised with the constructor due to LOGGER being a final field.
     */
    @InjectMocks
    LoggingInterceptor interceptor = new LoggingInterceptor(mockLogger);

    @Test
    @DisplayName("Tests the interceptor invokes the Request Logger to pre handle the start of the request")
    void test_preHandle_Invokes_RequestLogger() {
        assertTrue(interceptor.preHandle(mockRequest, mockResponse, mockHandler));
        verify(mockRequestLogger, times(1)).logStartRequestProcessing(
                mockRequest,
                mockLogger
        );
    }

    @Test
    @DisplayName("Tests the interceptor invokes the Request Logger to post handle the end of the request")
    void test_postHandle_Invokes_RequestLogger() {
        interceptor.postHandle(mockRequest, mockResponse, mockHandler, mockModelAndView);
        verify(mockRequestLogger, times(1)).logEndRequestProcessing(
                mockRequest,
                mockResponse,
                mockLogger
        );

    }

    @Test
    void test_constructor_doesnt_Error() {
        LoggingInterceptor newInterceptor = new LoggingInterceptor();
        assertNotNull(newInterceptor);
    }
}