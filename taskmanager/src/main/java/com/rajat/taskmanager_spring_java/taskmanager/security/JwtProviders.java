package com.rajat.taskmanager_spring_java.taskmanager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
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

    public JwtProviders() { }

    public static String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        Date now = new Date(System.currentTimeMillis()); // Use the authentication time as the reference time
        Date expirationTime = new Date(now.getTime() + 86400000); // 1 day expiry

        String jwt = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(secretKey)
                .compact();

        System.out.println("JwtProviders generateToken()  JWT:- " + jwt);
        JwtProviders.jwtToken = jwt;

        return jwt;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if(authorities == null) return null;

        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}