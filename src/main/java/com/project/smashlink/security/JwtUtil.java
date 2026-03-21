package com.project.smashlink.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry.ms}")
    private long expiryMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, String role){
//        return Jwts.builder()
//                .subject(email)
//                .claim("role", role)
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + expiryMs))
//                .signWith(getSigningKey())
//                .compact();


        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSigningKey(), Jwts.SIG.HS512) // ← explicitly HS512
                .compact();
    }

    public String extractEmail(String token){
        return  extractClaims(token).getSubject();
    }

    public String extractRole(String token){
        return  extractClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        }catch (Exception e){
            return  false;
        }
    }

//    private Claims extractClaims(String token){
////        return Jwts.parser()
////                .verifyWith(getSigningKey())
////                .build()
////                .parseSignedClaims(token)
////                .getPayload();
//
//
//    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
