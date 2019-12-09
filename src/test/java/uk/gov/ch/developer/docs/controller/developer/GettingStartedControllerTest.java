package uk.gov.ch.developer.docs.controller.developer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ch.developer.docs.ApplicationVariables;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GettingStartedControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GettingStartedController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get Getting Started Page - success path")
    void Test_GetRequest_ReturnsSuccess_ForCorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.GETTINGSTARTED_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ApplicationVariables.GETTINGSTARTED_VIEW));
    }

    @Test
    @DisplayName("Get Getting Started Page - Failure path")
    void Test_GetRequest_ReturnsError_ForIncorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.BADREQUEST_PATH))
                .andExpect(status().isNotFound());
    }
}