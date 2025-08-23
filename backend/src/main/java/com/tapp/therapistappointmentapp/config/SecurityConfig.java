package com.tapp.therapistappointmentapp.config;

import com.tapp.therapistappointmentapp.filter.JwtRequestFilter;
import com.tapp.therapistappointmentapp.service.UserDetailsServiceImpl;
// REMOVE: import com.tapp.therapistappointmentapp.security.AuthEntryPointJwt; // Make sure this is GONE

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // For @PreAuthorize
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder; // For static strategy

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Keep this for role-based authorization at controller level
public class SecurityConfig {

    // Explicitly set the SecurityContextHolder strategy (helps with consistency)
    static {
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
    }

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    // The AuthEntryPointJwt is NOT injected here (as it was deleted)

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
            .exceptionHandling(Customizer.withDefaults()) // Use default exception handling for 401/403
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions

            .authorizeHttpRequests(authorize -> authorize
                // Publicly accessible endpoints (no authentication required)
                .requestMatchers("/", "/api/patients/register", "/api/therapists/register", "/api/auth/login").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Swagger UI
                .requestMatchers("/api/therapists/*/schedule").permitAll() // Public schedule view
                .requestMatchers("/api/debug/**").permitAll() // Debug endpoints for JWT testing

                // --- TEMPORARY: MAKE REVIEW SUBMISSION PERMITALL FOR TESTING ---
                // This is to see if the problem is ONLY with the authentication for this specific endpoint.
                // If this works, the issue is with its @PreAuthorize or its interaction with the auth filter.
                .requestMatchers("/api/patients/appointments/*/reviews").permitAll() // TEMPORARY: Make reviews public
                // --- END TEMPORARY ---

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Explicitly disable default authentication mechanisms
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(formLogin -> formLogin.disable())
            .anonymous(anonymous -> anonymous.disable()); // Disable AnonymousAuthenticationFilter

        // Register our custom authentication provider
        http.authenticationProvider(authenticationProvider());

        // Place our JWT filter before Spring Security's default UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}