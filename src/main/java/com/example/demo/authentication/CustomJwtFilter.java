package com.example.demo.authentication;

import com.example.demo.jwt_service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Original source: https://medium.com/code-with-farhan/spring-security-jwt-authentication-authorization-a2c6860be3cf
@Component
public class CustomJwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    public CustomJwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String bearerToken = jwtService.extractTokenFromRequestHeader(request);
            if (bearerToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtService.extractClaimsFromToken(request);
            if (claims != null && jwtService.validateToken(claims)) {
                String username = jwtService.getUsername(claims);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username,
                        "",
                        List.of(new SimpleGrantedAuthority(jwtService.getRole(claims)))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exception) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }

        filterChain.doFilter(request, response);
    }
}
