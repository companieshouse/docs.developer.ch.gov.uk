package uk.gov.ch.developer.docs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "REDIRECT_URI=/")
class DocsWebApplicationTest {

    @Autowired
    private DocsWebApplication app;

    @Test
    void contextLoads() {
        assertNotNull(app);
    }
}