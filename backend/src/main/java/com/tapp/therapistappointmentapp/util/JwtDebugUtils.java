package com.tapp.therapistappointmentapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtDebugUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtDebugUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    private byte[] getKeyBytes() {
        return jwtSecret.getBytes();
    }

    /**
     * Decode JWT token and return claims without validation
     */
    public Claims decodeToken(String token) {
        try {
            return Jwts.parser().setSigningKey(getKeyBytes()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            logger.error("Failed to decode token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = decodeToken(token);
            if (claims == null) return true;
            
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Get token expiration time
     */
    public Date getTokenExpiration(String token) {
        try {
            Claims claims = decodeToken(token);
            return claims != null ? claims.getExpiration() : null;
        } catch (Exception e) {
            logger.error("Error getting token expiration: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get token issued time
     */
    public Date getTokenIssuedAt(String token) {
        try {
            Claims claims = decodeToken(token);
            return claims != null ? claims.getIssuedAt() : null;
        } catch (Exception e) {
            logger.error("Error getting token issued time: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get subject (username/email) from token
     */
    public String getTokenSubject(String token) {
        try {
            Claims claims = decodeToken(token);
            return claims != null ? claims.getSubject() : null;
        } catch (Exception e) {
            logger.error("Error getting token subject: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Comprehensive token validation and debugging
     */
    public void debugToken(String token) {
        logger.info("=== JWT Token Debug Information ===");
        
        // Check if token is null or empty
        if (token == null || token.trim().isEmpty()) {
            logger.error("Token is null or empty");
            return;
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        logger.info("Token (first 50 chars): {}", token.substring(0, Math.min(50, token.length())) + "...");

        // Decode token
        Claims claims = decodeToken(token);
        if (claims == null) {
            logger.error("Failed to decode token");
            return;
        }

        // Display token information
        logger.info("Subject (email): {}", claims.getSubject());
        logger.info("Issued At: {}", claims.getIssuedAt());
        logger.info("Expiration: {}", claims.getExpiration());
        
        // Check expiration
        boolean isExpired = isTokenExpired(token);
        logger.info("Is Expired: {}", isExpired);
        
        if (isExpired) {
            logger.warn("Token has expired!");
        } else {
            Date now = new Date();
            Date expiration = claims.getExpiration();
            long timeLeft = expiration.getTime() - now.getTime();
            logger.info("Time left: {} minutes", timeLeft / (1000 * 60));
        }

        logger.info("=== End JWT Token Debug ===");
    }
}

