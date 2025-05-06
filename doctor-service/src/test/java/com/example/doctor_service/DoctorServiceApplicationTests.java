package com.hospital.doctor_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // 👈 ensures it uses application-test.properties from test/resources
class DoctorServiceApplicationTests {

	@Test
	void contextLoads() {
		// This test will pass if Spring context loads correctly
	}
}
