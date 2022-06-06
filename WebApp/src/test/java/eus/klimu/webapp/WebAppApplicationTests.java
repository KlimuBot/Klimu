package eus.klimu.webapp;

import eus.klimu.WebAppApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class WebAppApplicationTests {

    @Test
    void contextLoads() {
        WebAppApplication webAppApplication = new WebAppApplication();
        assertThat(webAppApplication.passwordEncoder()).isNotNull();
    }

}
