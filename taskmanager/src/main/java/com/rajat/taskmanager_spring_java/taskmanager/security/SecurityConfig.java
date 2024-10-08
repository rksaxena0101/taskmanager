package com.rajat.taskmanager_spring_java.taskmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtTokenValidator jwtTokenValidator;

    @Autowired
    SecurityConfig(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeRequests(
//                        authorize -> authorize.requestMatchers("/api/**")
//                                .authenticated().anyRequest().permitAll())
//                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()));
//        //.httpBasic(Customizer.withDefaults())
//        //.formLogin(Customizer.withDefaults());
//        return http.build();
//    }

    String[] publicRoutes = { "/auth/login", "/auth/register" }; // Add more public endpoints here
    String[] privateRoutes = { "/api/**" };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless applications
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicRoutes).permitAll() // Allow public access to the login endpoint
                        .requestMatchers(privateRoutes).authenticated() // Require authentication for API routes
                        .anyRequest().permitAll() // Allow all other requests
                )
                //.addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class);
                .addFilterBefore(jwtTokenValidator, BasicAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration ccfg = new CorsConfiguration();
                ccfg.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
                ccfg.setAllowedMethods(Collections.singletonList("*"));
                ccfg.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                ccfg.setExposedHeaders(Arrays.asList("Authorization"));
                ccfg.setAllowCredentials(true);
                ccfg.setMaxAge(3600L);
                return ccfg;

            }
        };

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}