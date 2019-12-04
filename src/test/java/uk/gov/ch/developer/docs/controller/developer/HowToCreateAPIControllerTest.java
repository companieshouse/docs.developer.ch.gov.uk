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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HowToCreateAPIControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HowToCreateAPIController controller;

    //    @Value("${howToCreate.path}")
    private String path = "/dev-hub/create-api";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get How To Create API Home Page - success path")
    void Test_GetRequest_ReturnsSuccess_ForCorrectPath() throws Exception {
        System.out.println(path);
        this.mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(view().name("dev-hub/createApi"));
    }

    @Test
    @DisplayName("Get How To Create API Home Page - Failure path")
    void Test_GetRequest_ReturnsError_ForIncorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.BADREQUEST_PATH))
                .andExpect(status().isNotFound());
    }
}