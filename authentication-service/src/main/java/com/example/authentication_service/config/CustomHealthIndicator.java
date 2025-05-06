package com.example.authentication_service.config;

import com.example.authentication_service.repository.UserRepository;
import com.example.authentication_service.security.JwtUtil;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CustomHealthIndicator(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Health health() {
        Health.Builder status = Health.up();

        try {
            userRepository.count();
            status.withDetail("database", "available");
        } catch (Exception e) {
            status.withDetail("database", "unavailable - " + e.getMessage());
        }

        try {
            // ✅ Updated to match new method signature
            jwtUtil.generateToken("health-check", "USER", 0L);
            status.withDetail("jwt-service", "operational");
        } catch (Exception e) {
            status.withDetail("jwt-service", "failed - " + e.getMessage());
        }

        return status.build();
    }
}
