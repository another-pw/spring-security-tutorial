package com.example.demo.jwt_service;

import com.example.demo.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Original source: https://medium.com/code-with-farhan/spring-security-jwt-authentication-authorization-a2c6860be3cf
@Service
public class JwtService {

    private static final String SECRET_KEY = "your_secret_key";

    private static final String TOKEN_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final long EXPIRATION_TIME = 3600 * 1_000;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String extractTokenFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public Claims extractClaimsFromToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequestHeader(request);
            if (token != null) {
                return parseJwtClaims(token);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public boolean validateToken(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public String getUsername(Claims claims) {
        return claims.getSubject();
    }

    public String getRole(Claims claims) {
        return claims.get("role").toString();
    }
}
