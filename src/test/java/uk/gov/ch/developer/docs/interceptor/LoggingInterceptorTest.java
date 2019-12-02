package uk.gov.ch.developer.docs.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.RequestLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
    @Mock
    Logger mockLogger;
    @Mock
    ModelAndView mockModelAndView;

    @InjectMocks
    LoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_preHandle_Invokes_RequestLogger() {
        assertTrue(interceptor.preHandle(mockRequest, mockResponse, mockHandler));
        verify(mockRequestLogger, times(1)).logStartRequestProcessing(mockRequest, mockLogger);
    }

    @Test
    void test_postHandle_Invokes_RequestLogger() {
        interceptor.postHandle(mockRequest, mockResponse, mockHandler, mockModelAndView);
        verify(mockRequestLogger, times(1)).logEndRequestProcessing(mockRequest, mockResponse, mockLogger);
    }
}