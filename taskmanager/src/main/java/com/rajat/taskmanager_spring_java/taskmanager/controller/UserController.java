package com.rajat.taskmanager_spring_java.taskmanager.controller;

import com.rajat.taskmanager_spring_java.taskmanager.dtos.UserDTO;
import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import com.rajat.taskmanager_spring_java.taskmanager.entity.UserRole;
import com.rajat.taskmanager_spring_java.taskmanager.repository.UserRepository;
import com.rajat.taskmanager_spring_java.taskmanager.security.AuthResponse;
import com.rajat.taskmanager_spring_java.taskmanager.security.JwtTokenValidator;
import com.rajat.taskmanager_spring_java.taskmanager.service.UserService;
import com.rajat.taskmanager_spring_java.taskmanager.service.imp.UserServiceImplementation;
import com.rajat.taskmanager_spring_java.taskmanager.security.JwtProviders;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserServiceImplementation customUserDetails;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    private JwtProviders jwtProviders;

    Collection<? extends GrantedAuthority> authorities;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String mobile = user.getMobile();
        String role = user.getRole();

        User isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setMobile(mobile);
        createdUser.setPassword(passwordEncoder.encode(password));

        // Set role directly in User entity
        createdUser.setRole(role); // Default role

        User savedUser = userRepository.save(createdUser);
        // Update the authentication part to reflect the saved user
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProviders.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        //System.out.println("Username:- "+loginRequest.getEmail()+"-------"+loginRequest.getPassword());
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(username,password);
        if(authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities(); // Correct usage here <>
            System.out.println("Authorities during JWT generation: " + authorities.toString());
        }
        //System.out.println(username+"-------"+password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProviders.generateToken(authentication);
        System.out.println("UserController signin() token"+token);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login success");
        authResponse.setEmail(username);
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }


    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username and password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{email}")
    public ResponseEntity<User> getUser(@PathVariable("email") String username) {
        User user = userService.findAllUsers()
                .stream()
                .filter(u -> u.getEmail().equals(username))
                .findFirst()
                .orElse(null);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getFullName(), user.getEmail(), user.getMobile(), user.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }
}