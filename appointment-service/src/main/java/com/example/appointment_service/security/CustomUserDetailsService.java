package com.example.appointment_service.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // 🔐 Use only for validating token presence, role is enforced by JwtService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Return user with empty password and generic role — actual role check is in JwtAuthenticationFilter
        return new User(
                username,
                "",  // password is not needed because JWT already authenticated it
                Collections.singleton(new SimpleGrantedAuthority("PATIENT")) // default
        );
    }
}
