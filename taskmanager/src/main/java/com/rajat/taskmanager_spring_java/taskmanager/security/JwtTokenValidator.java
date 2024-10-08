package com.rajat.taskmanager_spring_java.taskmanager.security;

import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import com.rajat.taskmanager_spring_java.taskmanager.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
import java.util.Enumeration;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public JwtTokenValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractJwtFromRequest(request); // Extract JWT directly from the request
        System.out.println("JwtTokenValidator::doFilterInternal request:"+request);
        System.out.println("JwtTokenValidator::doFilterInternal JWT before if:- " + jwt);
        // Validate JWT
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove "Bearer " prefix
            System.out.println("JwtTokenValidator::doFilterInternal JWT inside if:- " + jwt);
            try {
                SecretKey key = JwtProviders.secretKey;
                System.out.println("JwtTokenValidator::doFilterInternal Using SecretKey: " + key);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                System.out.println("JwtTokenValidator::doFilterInternal Claims:- " + claims);
                String email = String.valueOf(claims.get("email"));
                System.out.println("Email from JWT claims: " + email);

                String roles = String.valueOf(claims.get("authorities"));
                if (roles == null || roles.isEmpty()) {
                    roles = "ROLE_CUSTOMER";  // Fallback to a default role
                }

                System.out.println("Roles from JWT claims: " + roles);
                List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
                System.out.println("Granted authorities from JWT: " + auth);

                if (auth.isEmpty()) {
                    // Set default authorities if not present in the JWT
                    auth = AuthorityUtils.createAuthorityList("ROLE_CUSTOMER");
                    System.out.println("No authorities found in JWT. Setting default authorities: " + auth);
                }

                // Fetch user from the database using the email
                User user = userRepository.findByEmail(email);
                System.out.println("JwtTokenValidator::doFilterInternal User fetched from DB: " + user);
                if (user != null) {
                    System.out.println("JwtTokenValidator::doFilterInternal Setting authentication for user: " + user.getEmail());
                    // Set the entire User entity as the principal
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, auth);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("SecurityContext after setting auth: " + SecurityContextHolder.getContext().getAuthentication());
                    System.out.println("Authentication set for user: " + user.getEmail());
                } else {
                    throw new RuntimeException("User not found for email: " + email);
                }

            } catch (ExpiredJwtException e) {
                System.err.println("Token has expired: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired");
                return;  // Exit to prevent further processing
            } catch (SignatureException e) {
                System.err.println("Invalid JWT signature: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return;  // Exit to prevent further processing
            } catch (Exception e) {
                System.err.println("Invalid token: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;  // Exit to prevent further processing
            }
        }

        filterChain.doFilter(request, response);  // Continue the filter chain
    }

    // Helper method to extract the JWT from the request
    private String extractJwtFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        System.out.println("Authorization header before if: " + header); // Log the header value
        if (header == null || header.isEmpty()) {
            return null;
        }
        if (header != null && header.startsWith("Bearer ")) {
            System.out.println("JwtTokenValidator::extractJwtFromRequest Authorization header starts with 'Bearer ' jwt token:- "+header);
            System.out.println("JwtTokenValidator::extractJwtFromRequest Authorization header after substring jwt token:- "+header.substring(7));
            return header.substring(7); // Remove "Bearer " prefix
        } else {
            System.out.println("Authorization header does not start with Bearer or is missing");
        }
        return null;
    }
}