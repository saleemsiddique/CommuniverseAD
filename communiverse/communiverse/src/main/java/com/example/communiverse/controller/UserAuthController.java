package com.example.communiverse.controller;

import com.example.communiverse.domain.User;
import com.example.communiverse.domain.UserInteractions;
import com.example.communiverse.payload.request.UserLoginRequest;
import com.example.communiverse.payload.request.UserSignupRequest;
import com.example.communiverse.payload.response.JwtResponse;
import com.example.communiverse.payload.response.MessageResponse;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.security.jwt.JwtUtils;
import com.example.communiverse.security.services.UserDetailsImpl;
import com.example.communiverse.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


//https://github.com/bezkoder/spring-boot-spring-security-jwt-authentication
//http://localhost:8080/api/auth/signup?username=Pepe&email=pepe@gmail.com&password=1234&role=admin
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        String loginInput = userLoginRequest.getEmailOrUsername();
        Authentication authentication = null;

        try {
            User user = userRepository.findByUsernameOrEmail(loginInput, loginInput)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email or username : " + loginInput));

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getEmailOrUsername(), userLoginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getLastname(),
                    userDetails.getEmail(),
                    userDetails.getPassword(),
                    userDetails.getUsername()));
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("User not found with email or username: " + loginInput);
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupRequest signUpRequestUser) {
        if (userRepository.existsByEmail(signUpRequestUser.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already been used");
        }

        if (userRepository.existsByUsername(signUpRequestUser.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already been used");
        }

        String randomId = UUID.randomUUID().toString().split("-")[0];
        // Create new user's account
        User user = new User(randomId, signUpRequestUser.getName(), signUpRequestUser.getLastName(), signUpRequestUser.getEmail(),
                encoder.encode(signUpRequestUser.getPassword()), signUpRequestUser.getUsername());

        userRepository.save(user);

        ObjectMapper objectMapper = new ObjectMapper();
        String clienteJson;
        try {
            clienteJson = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al convertir el objeto Cliente a JSON"));
        }

        // Devolver el JSON del cliente en el cuerpo de la respuesta
        return ResponseEntity.ok(clienteJson);
    }
}

