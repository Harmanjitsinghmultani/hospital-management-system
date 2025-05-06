package com.example.authentication_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Uses test profile to disable Eureka during tests
class AuthenticationServiceApplicationTests {

	@Test
	void contextLoads() {
		// Test will pass if the application context loads successfully
	}
}
