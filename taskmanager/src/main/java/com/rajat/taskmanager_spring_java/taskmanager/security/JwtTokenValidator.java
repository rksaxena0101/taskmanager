package com.rajat.taskmanager_spring_java.taskmanager.security;

import com.rajat.taskmanager_spring_java.taskmanager.controller.UserController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {
    private Authentication authentication;
    public JwtTokenValidator() {}

    public String getJwtToken() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("JwtTokenValidator::getJwtToken authentication:- " + this.authentication);
        if (this.authentication != null) {
            return JwtProviders.generateToken(this.authentication);
        } else {
            return null;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtToken();
        System.out.println("JwtTokenValidator::doFilterInternal JWT:- "+jwt);
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove "Bearer " prefix

            try {
                SecretKey key = JwtProviders.secretKey;

                System.out.println("JwtTokenValidator doFilterInternal JWT:- " + jwt);
                System.out.println("Using SecretKey: " + key);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();
                System.out.println("JWT Claims: " + claims);

                String email = String.valueOf(claims.get("email"));
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(String.valueOf(claims.get("authorities")));
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                System.err.println("Token has expired: " + e.getMessage());
                throw new BadCredentialsException("Token has expired", e);
            } catch (SignatureException e) {
                System.err.println("Invalid JWT signature: " + e.getMessage());
                throw new BadCredentialsException("Invalid JWT signature", e);
            } catch (Exception e) {
                System.err.println("Invalid token: " + e.getMessage());
                throw new BadCredentialsException("Invalid token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}