package uk.gov.ch.developer.docs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "REDIRECT_URI=http://dev.chs.local",
        "CHS_URL=http://chs.local",
        "DEVELOPER_URL=http://dev.chs.local"
})
class DocsWebApplicationTest {

    @Autowired
    private DocsWebApplication app;

    @Test
    void contextLoads() {
        assertNotNull(app);
    }
}
