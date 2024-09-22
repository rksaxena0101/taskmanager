package com.rajat.taskmanager_spring_java.taskmanager.controller;

import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
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
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        String email = user.getEmail();
        String password = user.getPassword();
        String fullName = user.getFullName();
        String mobile = user.getMobile();
        String role = user.getRole();

//        User isEmailExist = userRepository.findByEmail(email);
//        if (isEmailExist != null) {
//            throw new Exception("Email Is Already Used With Another Account");
//        }
        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setMobile(mobile);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(createdUser);
        userRepository.save(savedUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProviders.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        //System.out.println("Username:- "+loginRequest.getEmail()+"-------"+loginRequest.getPassword());
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticate(username,password);
        //System.out.println(username+"-------"+password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProviders.generateToken(authentication);
        System.out.println("UserController signin() token"+token);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse,HttpStatus.OK);
    }


    private Authentication authenticate(String username, String password) {

        System.out.println(username+"---++----"+password);

        UserDetails userDetails = customUserDetails.loadUserByUsername(username);

        System.out.println("Sig in in user details"+ userDetails);

        if(userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);

            throw new BadCredentialsException("Invalid username and password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch"+userDetails);

            throw new BadCredentialsException("Invalid password");

        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) {
        User user = userService.findAllUsers()
                .stream()
                .filter(u -> u.getEmail().equals(username))
                .findFirst()
                .orElse(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

}