package com.example.communiverse.controller;

import com.example.communiverse.domain.User;
import com.example.communiverse.exception.AccountViaGoogleException;
import com.example.communiverse.payload.request.*;
import com.example.communiverse.payload.response.JwtResponse;
import com.example.communiverse.payload.response.MessageResponse;
import com.example.communiverse.repository.UserRepository;
import com.example.communiverse.security.jwt.JwtUtils;
import com.example.communiverse.security.services.UserDetailsImpl;
import com.example.communiverse.service.BlobStorageService;
import com.example.communiverse.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    UserService userService;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private BlobStorageService blobStorageService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        String loginInput = userLoginRequest.getEmailOrUsername().toLowerCase();

        try {
            User user = userRepository.findByUsernameOrEmail(loginInput, loginInput)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email or username: " + loginInput));

            if (userLoginRequest.isGoogle()) {
                // Lógica para usuarios de Google
                if (!user.isGoogle()) {
                    return ResponseEntity
                            .badRequest()
                            .body("This user is not registered via Google");
                }

                // Generar token JWT directamente
                String jwt = jwtUtils.generateJwtTokenUser(user);

                return ResponseEntity.ok(new JwtResponse(jwt,
                        user.getId(),
                        user.getName(),
                        user.getLastName(),
                        user.getEmail().toLowerCase(),
                        user.getPassword(),
                        user.getUsername().toLowerCase(),
                        user.isGoogle()
                ));
            } else {

                if (user.isGoogle()){
                    return ResponseEntity
                            .badRequest()
                            .body("User not found with email or username: " + loginInput);
                }

                // Lógica para usuarios normales
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(userLoginRequest.getEmailOrUsername().toLowerCase(), userLoginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                return ResponseEntity.ok(new JwtResponse(jwt,
                        userDetails.getId(),
                        userDetails.getName(),
                        userDetails.getLastname(),
                        userDetails.getEmail().toLowerCase(),
                        userDetails.getPassword(),
                        userDetails.getUsername().toLowerCase(),
                        user.isGoogle()
                ));
            }
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("User not found with email or username: " + loginInput);
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("Email, username, or password is incorrect");
        }
    }



    private ResponseEntity<?> confirmingEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email format is invalid");
        }
        return null; // Retorna null si el formato es válido
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserSignupRequest signUpRequestUser) {
        ResponseEntity<?> emailValidationResult = confirmingEmail(signUpRequestUser.getEmail());
        if (emailValidationResult != null) {
            return emailValidationResult;
        }

        if (userRepository.existsByEmail(signUpRequestUser.getEmail().toLowerCase())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already been used");
        }

        if (userRepository.existsByUsername(signUpRequestUser.getUsername().toLowerCase())) {
            return ResponseEntity
                    .badRequest()
                    .body("Username is already been used");
        }

        // Create new user's account
        User user = new User(signUpRequestUser.getName(), signUpRequestUser.getLastName(), signUpRequestUser.getEmail().toLowerCase(),
                encoder.encode(signUpRequestUser.getPassword()), signUpRequestUser.getUsername().toLowerCase(), signUpRequestUser.isGoogle());

        userService.addUser(user);

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

    @PutMapping("edit/{id}")
    public ResponseEntity<?> modifyUser(@PathVariable String id, @Valid @RequestBody UserModifyRequest userModifyRequest) {

        if(userModifyRequest.getName().length() > 20){
            return ResponseEntity
                    .badRequest()
                    .body("Name is too long (max 20 characters)");
        }

        if(userModifyRequest.getLastName().length() > 20){
            return ResponseEntity
                    .badRequest()
                    .body("Last Name is too long (max 20 characters)");
        }

        if(userModifyRequest.getUsername().length() > 20){
            return ResponseEntity
                    .badRequest()
                    .body("Username is too long (max 20 characters)");
        }

        if(userModifyRequest.getBiography().length() > 100){
            return ResponseEntity
                    .badRequest()
                    .body("Description is too long (max 100 characters)");
        }

        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: User not found"));
        }

        // Obtiene el cliente existente
        User user = optionalUser.get();

        if (!user.getUsername().equalsIgnoreCase(userModifyRequest.getUsername())) {
            if (userRepository.existsByUsername(userModifyRequest.getUsername().toLowerCase())) {
                return ResponseEntity
                        .badRequest()
                        .body("Error: Username is already been used");
            }
        }

        // Actualiza los campos del cliente con los valores proporcionados en la solicitud
        user.setName(userModifyRequest.getName());
        user.setLastName(userModifyRequest.getLastName());
        user.setBiography(userModifyRequest.getBiography());
        user.setUsername(userModifyRequest.getUsername().toLowerCase());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Cliente modificado exitosamente"));
    }

    @PutMapping("editUserCommunities/{id}")
    public ResponseEntity<?> modifyUserCommunities(@PathVariable String id, @Valid @RequestBody UserCommunitiesModifyRequest userCommunitiesModifyRequest) {

        Optional<User> optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: User not found"));
        }

        // Obtiene el cliente existente
        User user = optionalUser.get();

        // Actualiza los campos del cliente con los valores proporcionados en la solicitud
        user.setCreatedCommunities(userCommunitiesModifyRequest.getCreatedCommunities());
        user.setModeratedCommunities(user.getModeratedCommunities());
        user.setMemberCommunities(userCommunitiesModifyRequest.getMemberCommunities());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Cliente modificado exitosamente"));
    }

    @PutMapping("editphoto/{id}")
    public ResponseEntity<?> modifyUserPhoto(@PathVariable String id, @RequestBody String base64Image) {
        String photoUrl = "";
        try {
            Optional<User> optionalUser = userRepository.findById(id);

            if (!optionalUser.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Error: User not found"));
            }

            // Obtiene el usuario existente
            User user = optionalUser.get();

            // Verifica si la cadena base64 de la imagen es válida
            if (!base64Image.equalsIgnoreCase("{\"\":\"\"}")) {
                // Sube la nueva imagen del usuario al almacenamiento de blobs
                photoUrl = blobStorageService.uploadPhoto(base64Image, id + "-profile-photo.jpg");
            }
            user.setPhoto(photoUrl);

            // Guarda los cambios en el usuario en la base de datos
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("User photo modified successfully"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error modifying user photo: " + e.getMessage()));
        }
    }

    @PutMapping("/editPassword/{email}")
    public ResponseEntity<?> modifyUserPassword(@PathVariable String email, @Valid @RequestBody UserPasswordRequest userPasswordRequest){
        String loginInput = userPasswordRequest.getEmailOrUsername().toLowerCase();

        try {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        // Lógica para usuarios normales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginInput, userPasswordRequest.getPassword()));

        User user = optionalUser.get();

        user.setPassword(encoder.encode(userPasswordRequest.getNewPassword()));
        userRepository.save(user);
    } catch (UsernameNotFoundException ex) {
        return ResponseEntity
                .badRequest()
                .body("User not found with email or username: " + loginInput);
    } catch (BadCredentialsException ex) {
        return ResponseEntity
                .badRequest()
                .body("Current password is incorrect");
    }

        return ResponseEntity.ok(new MessageResponse("Cliente modificado exitosamente"));
    }


    @GetMapping("/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@PathVariable String email){
        try {
            String responseMessage = userService.recuperatePassword(email);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch(AccountViaGoogleException err){
            return ResponseEntity
                    .badRequest()
                    .body("Cannot recover password from an account registered via Google.");
        } catch (Exception e){
            return ResponseEntity
                    .badRequest()
                    .body("There is no email with this account.");
        }
    }

}

