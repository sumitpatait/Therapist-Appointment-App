package com.tapp.therapistappointmentapp.controller;

import com.tapp.therapistappointmentapp.util.JwtDebugUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class JwtDebugController {

    private final JwtDebugUtils jwtDebugUtils;

    public JwtDebugController(JwtDebugUtils jwtDebugUtils) {
        this.jwtDebugUtils = jwtDebugUtils;
    }

    @PostMapping("/jwt/validate")
    public ResponseEntity<Map<String, Object>> validateJwtToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.trim().isEmpty()) {
            response.put("valid", false);
            response.put("error", "Token is null or empty");
            return ResponseEntity.badRequest().body(response);
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // Get token information
            String subject = jwtDebugUtils.getTokenSubject(token);
            boolean isExpired = jwtDebugUtils.isTokenExpired(token);

            response.put("valid", !isExpired);
            response.put("subject", subject);
            response.put("expired", isExpired);
            response.put("expiration", jwtDebugUtils.getTokenExpiration(token));
            response.put("issuedAt", jwtDebugUtils.getTokenIssuedAt(token));

            if (isExpired) {
                response.put("error", "Token has expired");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("valid", false);
            response.put("error", "Token validation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/jwt/decode")
    public ResponseEntity<Map<String, Object>> decodeJwtToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.trim().isEmpty()) {
            response.put("success", false);
            response.put("error", "Token is null or empty");
            return ResponseEntity.badRequest().body(response);
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // Log debug information
            jwtDebugUtils.debugToken(token);

            // Get token information
            String subject = jwtDebugUtils.getTokenSubject(token);
            boolean isExpired = jwtDebugUtils.isTokenExpired(token);

            response.put("success", true);
            response.put("subject", subject);
            response.put("expired", isExpired);
            response.put("expiration", jwtDebugUtils.getTokenExpiration(token));
            response.put("issuedAt", jwtDebugUtils.getTokenIssuedAt(token));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Token decoding failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

