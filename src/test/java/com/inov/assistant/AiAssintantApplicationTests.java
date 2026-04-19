package com.inov.assistant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	/*properties = {
        "spring.ai.openai.enabled=false"
    }*/
)
@ActiveProfiles("test")
class AiAssintantApplicationTests {

	@Test
	void contextLoads() {
	}

}
