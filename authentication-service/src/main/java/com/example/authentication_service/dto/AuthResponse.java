package com.example.authentication_service.dto;

public class AuthResponse {
    private String token;
    private String message;
    private String email;
    private String role;
    private Long id;         // ✅ Added for userId
    private String username; // ✅ Added for username

    // Default constructor
    public AuthResponse() {}

    // Constructor for login response
    public AuthResponse(String token, String email, String role, Long id, String username) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.id = id;
        this.username = username;
    }

    // Constructor for registration response
    public AuthResponse(String token, String message, String email, String role, Long id, String username) {
        this.token = token;
        this.message = message;
        this.email = email;
        this.role = role;
        this.id = id;
        this.username = username;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='****'" +
                ", message='" + message + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
