package com.example.authentication_service.service;

import com.example.authentication_service.client.DoctorClient;
import com.example.authentication_service.dto.DoctorDTO;
import com.example.authentication_service.dto.RegisterRequest;
import com.example.authentication_service.enums.Role;
import com.example.authentication_service.model.User;
import com.example.authentication_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorClient doctorClient;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       DoctorClient doctorClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.doctorClient = doctorClient;
    }

    public User registerUser(RegisterRequest request) {
        validateRegistrationRequest(request);
        checkForExistingUsers(request.getEmail(), request.getUsername());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        // ➡️ Only create doctor immediately (patients created after verification)
        if (request.getRole() == Role.DOCTOR) {
            DoctorDTO doctorDTO = new DoctorDTO(
                    request.getUsername(),
                    request.getSpecialization() != null ? request.getSpecialization() : "General",
                    request.getEmail(),
                    request.getPhone() != null ? request.getPhone() : "N/A"
            );
            doctorClient.createDoctor(doctorDTO);
        }

        return savedUser;
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        if (request.getRole() == Role.DOCTOR) {
            if (request.getSpecialization() == null || request.getSpecialization().isBlank()) {
                throw new IllegalArgumentException("Specialization is required for doctors");
            }
            if (request.getPhone() == null || request.getPhone().isBlank()) {
                throw new IllegalArgumentException("Phone number is required for doctors");
            }
        }
    }

    private void checkForExistingUsers(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already taken: " + username);
        }
    }
}
