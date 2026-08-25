package com.product.Security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String SECRET =
            "mysecretkeymysecretkeymysecretkey123456";

    private static final long EXPIRATION =
            1000 * 60 * 60; // 1 hour

    private Key getKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generate JWT with User ID, Username and Role
    public String generateToken(Long userId, String username, String role) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION)
                )
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Get all claims
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract Username
    public String extractUsername(String token) {

        return getClaims(token).getSubject();
    }

    // Extract User ID
    public Long extractUserId(String token) {

        Number userId = getClaims(token).get("userId", Number.class);

        return userId != null ? userId.longValue() : null;
    }

    // Extract Role
    public String extractRole(String token) {

        return getClaims(token).get("role", String.class);
    }

    // Validate Token
    public boolean validateToken(String token, String username) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username);
    }
}