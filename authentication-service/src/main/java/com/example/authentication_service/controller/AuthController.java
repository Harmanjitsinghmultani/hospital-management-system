package com.example.authentication_service.controller;

import com.example.authentication_service.client.PatientClient;
import com.example.authentication_service.dto.AuthRequest;
import com.example.authentication_service.dto.AuthResponse;
import com.example.authentication_service.dto.PatientDTO;
import com.example.authentication_service.dto.RegisterRequest;
import com.example.authentication_service.enums.Role;
import com.example.authentication_service.model.User;
import com.example.authentication_service.repository.UserRepository;
import com.example.authentication_service.security.JwtUtil;
import com.example.authentication_service.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Transactional
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PatientClient patientClient;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PatientClient patientClient) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.patientClient = patientClient;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User savedUser = userService.registerUser(request);
            logger.info("Registration successful for user: {}", savedUser.getEmail());

            AuthResponse response = new AuthResponse();
            response.setMessage("User registered successfully! Please check your email for verification.");
            response.setEmail(savedUser.getEmail());
            response.setRole(savedUser.getRole().name());
            response.setId(savedUser.getId());
            response.setUsername(savedUser.getUsername());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Validation error during registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during registration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> userOpt = userRepository.findByUsernameOrEmail(userDetails.getUsername());

            if (userOpt.isEmpty()) {
                logger.warn("User not found with username: {}", userDetails.getUsername());
                return ResponseEntity.status(401).body("Invalid username/email or password");
            }

            User user = userOpt.get();

            if (!user.isEmailVerified()) {
                logger.warn("Login attempt with unverified email: {}", user.getEmail());
                return ResponseEntity.status(403).body("Email not verified. Please verify your email first.");
            }

            // ✅ Updated to include userId in token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getId());
            logger.info("Login successful for user: {}", user.getUsername());

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    "Login successful",
                    user.getEmail(),
                    user.getRole().name(),
                    user.getId(),
                    user.getUsername()
            ));

        } catch (BadCredentialsException e) {
            logger.warn("Invalid login attempt: {}", e.getMessage());
            return ResponseEntity.status(401).body("Invalid username/email or password");
        } catch (Exception e) {
            logger.error("Login failed due to error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Login failed due to server error");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            Optional<User> optionalUser = userRepository.findByVerificationToken(token);

            if (optionalUser.isEmpty()) {
                logger.warn("Invalid or expired email verification token: {}", token);
                return ResponseEntity.badRequest().body("Invalid or expired token.");
            }

            User user = optionalUser.get();
            if (user.isEmailVerified()) {
                logger.info("Email already verified for user: {}", user.getEmail());
                return ResponseEntity.ok("Email is already verified.");
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            if (user.getRole() == Role.PATIENT) {
                PatientDTO patientDTO = new PatientDTO(user.getUsername(), user.getEmail(), "N/A");
                patientClient.createPatient(patientDTO);
                logger.info("Patient record created in patient-service for: {}", user.getEmail());
            }

            logger.info("Email verified successfully for user: {}", user.getEmail());
            return ResponseEntity.ok("Email verified successfully!");
        } catch (Exception e) {
            logger.error("Email verification failed due to error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Email verification failed due to server error.");
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            if (user.isEmailVerified()) {
                logger.info("Email already verified for: {}", email);
                return ResponseEntity.ok("Email is already verified.");
            }

            String newToken = UUID.randomUUID().toString();
            user.setVerificationToken(newToken);
            userRepository.save(user);

            String verificationLink = "http://localhost:8080/api/auth/verify?token=" + newToken;
            logger.info("Resent verification email to: {} - Link: {}", email, verificationLink);

            return ResponseEntity.ok("Verification email resent. Please check your inbox.");
        } catch (RuntimeException e) {
            logger.warn("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Resend verification failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error resending verification email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to resend verification email.");
        }
    }
}
