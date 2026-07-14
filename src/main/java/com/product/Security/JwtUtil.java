package com.product.Security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRET = "mysecretkeymysecretkeymysecretkey123456";

	private static final long EXPIRATION = 1000 * 60 * 60;

	private Key getKey() {

		return Keys.hmacShaKeyFor(SECRET.getBytes());
	}

	// Generate Token WITH ROLE
	public String generateToken(String username, String role) {

		Map<String, Object> claims = new HashMap<>();

		claims.put("role", role);

		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
				.signWith(getKey(), SignatureAlgorithm.HS256).compact();
	}

	// Extract Username
	public String extractUsername(String token) {

		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	// Extract Role
	public String extractRole(String token) {

		return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody().get("role",
				String.class);
	}

	// Validate Token
	public boolean validateToken(String token, String username) {

		String extractedUsername = extractUsername(token);

		return extractedUsername.equals(username);
	}
}