package com.tapp.therapistappointmentapp.service;

import com.tapp.therapistappointmentapp.dto.LoginRequest;
import com.tapp.therapistappointmentapp.dto.PatientRegistrationRequest;
import com.tapp.therapistappointmentapp.dto.JwtResponse;
import com.tapp.therapistappointmentapp.model.Patient;
import com.tapp.therapistappointmentapp.model.User;
import com.tapp.therapistappointmentapp.repository.UserRepository;
import com.tapp.therapistappointmentapp.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository; 

    public AuthService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
       
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

       
        SecurityContextHolder.getContext().setAuthentication(authentication);

  
        String jwt = jwtUtils.generateJwtToken(authentication);

     
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication!")); 

 
        return new JwtResponse(jwt, user.getId(), user.getEmail(), user.getRole().name());
    }

//	public Patient registerPatient(PatientRegistrationRequest request) {
//		// TODO Auto-generated method stub
//		
//		return null;
//	}
}