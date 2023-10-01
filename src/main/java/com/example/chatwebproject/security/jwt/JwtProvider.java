package com.example.chatwebproject.security.jwt;

import com.example.chatwebproject.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtProvider {
    @Value("${chatbot.jwt.secret.path}")
    private String keyPath;

    @Value("${chatbot.jwt.expiration}")
    private int expiredTime;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .signWith(key())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiredTime))
                .setClaims(setClaimsForToken(userDetails))
                .compact();
    }

    public Map<String, String> setClaimsForToken(UserDetailsImpl userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("USER_NAME", userDetails.getUsername());
        claims.put("ROLE", userDetails.getAuthorities().toArray()[0].toString());
        return claims;
    }

    @SneakyThrows
    public Key key() {
        byte[] bytes = Files.readAllBytes(Paths.get(keyPath));
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJwt(token)
                .getBody().get("USER_NAME").toString();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
