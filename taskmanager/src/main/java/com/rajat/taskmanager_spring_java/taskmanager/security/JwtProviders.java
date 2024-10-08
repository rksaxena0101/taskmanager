package com.rajat.taskmanager_spring_java.taskmanager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtProviders {
    // Generate a secure key with 256 bits for HS256
    static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
   static String jwtToken = "";

    public JwtProviders() {
        System.out.println("JwtProviders secretKey:- " + secretKey);
    }

    public static String generateToken(Authentication auth) {
        System.out.println("JwtProviders::generateToken Authorities during JWT generation start: " + auth.getAuthorities());
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        System.out.println("Authorities during JWT generation: " + authorities);

        String roles = populateAuthorities(authorities);
        System.out.println("Roles for JWT generation: " + roles);

        Date now = new Date(System.currentTimeMillis()); // Use the authentication time as the reference time
        Date expirationTime = new Date(now.getTime() + 86400000); // 1 day expiry

        String jwt = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .claim("email", auth.getName())
                .claim("authorities", roles != null ? roles : "ROLE_USER") // Ensure default role if empty
                .signWith(secretKey)
                .compact();

        System.out.println("Generated JWT: " + jwt);
        JwtProviders.jwtToken = jwt;

        return jwt;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        System.out.println("JwtProviders::populateAuthorities Authorities during JWT generation start: " + authorities.toString());
        if (authorities == null || authorities.isEmpty()) {
            System.out.println("No authorities found. Defaulting to ROLE_USER.");
            return "ROLE_CUSTOMER"; // Set default role if empty
        }

        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        String roles = String.join(",", auths);
        System.out.println("Populated authorities: " + roles);
        return roles;
    }
}