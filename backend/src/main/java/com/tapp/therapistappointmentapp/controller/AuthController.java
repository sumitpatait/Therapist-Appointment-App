package com.tapp.therapistappointmentapp.controller;

import com.tapp.therapistappointmentapp.dto.JwtResponse;
import com.tapp.therapistappointmentapp.dto.LoginRequest;
import com.tapp.therapistappointmentapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login") // Handles POST requests to /api/auth/login
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Invalid username or password!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error during authentication: " + e.getMessage());
        }
    }
}