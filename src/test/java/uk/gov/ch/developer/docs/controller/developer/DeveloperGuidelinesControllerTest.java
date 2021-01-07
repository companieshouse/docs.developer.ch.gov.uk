package uk.gov.ch.developer.docs.controller.developer;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.ch.developer.docs.ApplicationVariables;
import uk.gov.ch.developer.docs.DocsWebApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocsWebApplication.class)
@TestPropertySource(properties = "REDIRECT_URI=/")
class DeveloperGuidelinesControllerTest {

    private static final String URL = "/developer-guidelines";
    private static final String VIEW = "dev-hub/developerGuidelines";


    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @InjectMocks
    private DeveloperGuidelinesController controller;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("Get How To Obtain an API Home Page - success path")
    void Test_GetRequest_ReturnsSuccess_ForCorrectPath() throws Exception {
        this.mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(view().name(VIEW));
    }

    @Test
    @DisplayName("Get How To Obtain an API Home Page - Failure path")
    void Test_GetRequest_ReturnsError_ForIncorrectPath() throws Exception {
        this.mockMvc.perform(get(ApplicationVariables.BAD_REQUEST_URL))
                .andExpect(status().isNotFound());
    }

    @Test()
    @DisplayName("Get How To Obtain an API Home Page - Null path")
    void Test_GetRequest_ThrowsException_ForNullPath() {
        //Inspection suppressed because we are passing null to a @NotNull parameter.
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> this.mockMvc.perform(get(null)));
    }
}