package uk.gov.ch.developer.docs.controller.developer;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GettingStartedControllerollerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GettingStartedController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get Getting Started Page - success path")
    void Test_GetRequest_ReturnsSuccess_ForCorrectPathtRequest_ReturnsSuccess_ForCorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.GETTINGSTARTED_PATH))
                .andExpect(status().isOk());
    }
}