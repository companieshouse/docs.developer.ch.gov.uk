package uk.gov.ch.developer.docs.controller.developer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.ch.developer.docs.ApplicationVariables;
import uk.gov.ch.developer.docs.DocsWebApplication;
import uk.gov.ch.developer.docs.utility.TestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocsWebApplication.class)
class GetStartedControllerTest {

    private static final String PATH = "/get-started";
    private static final String VIEW = "dev-hub/getStarted";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;
    @InjectMocks
    private GetStartedController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Get Get Started Page - success path")
    void Test_GetRequest_ReturnsSuccess_ForCorrectPath() throws Exception {
        this.mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));
    }

    @Test
    @DisplayName("Get Get Started Page - Failure path")
    void Test_GetRequest_ReturnsError_ForIncorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.BADREQUEST_URL))
                .andExpect(status().isNotFound());
    }
}