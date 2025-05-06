package com.example.patient_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {PatientServiceApplication.class, TestJpaConfig.class})
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class PatientServiceApplicationTests {

	@Test
	void contextLoads() {
		// Test will pass if Spring context loads successfully
	}
}
