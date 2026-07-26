package com.networthtracker.backend.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.networthtracker.backend.entity.User;
import com.networthtracker.backend.repository.UserRepository;
import com.networthtracker.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

// runs on every request: reads the token and tells spring who the user is.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // no "Bearer <token>" header — let it pass through unauthenticated
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // strip off "Bearer " to get the raw token
        String token = authHeader.substring(7);

        try {
            // validate the token and pull out the user id
            Integer userId = jwtService.getUserIdFromToken(token);
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userOptional.get();

                // put the User into spring's security context = "this request is this user"
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (JWTVerificationException e) {
            // Token is invalid or expired — continue unauthenticated
        }

        filterChain.doFilter(request, response);
    }
}
