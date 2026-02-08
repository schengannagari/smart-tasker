package com.sc.smarttasker.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    private String jwtKey;

    @Value("${spring.security.jwt.expiration}")
    private long expirationMs;

    private Key key;

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> generateTokenWithExpiry(String username, Set<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("expiresAt", expiryDate.getTime()); // epoch millis

        return result;
    }


    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Set<String> extractRoles(String token) {
        return parseClaims(token).get("roles", Set.class);
    }

    public boolean validateToken(String token, String username) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(username)
                    && !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtKey));
        }
        return key;
    }
}

