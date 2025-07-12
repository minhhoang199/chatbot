package com.example.chatwebproject.security.jwt;

import com.example.chatwebproject.security.service.UserDetailImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Value("${demo.app.jwt.expiration}")
    private int expiredTime;
    @Value("${demo.app.jwt.secret.path}")
    private String jwtSecretKeyPath;

    public String generateJwtToken(Authentication authentication) {
        UserDetailImpl userDetail = (UserDetailImpl) authentication.getPrincipal();
        //Create token
        return Jwts.builder()
                .setClaims(setClaimForToken(userDetail))
                .signWith(key())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiredTime))
                .compact();

    }

    private Map<String, Object> setClaimForToken(UserDetailImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("USER_NAME", userDetails.getUsername());
        claims.put("ROLE", userDetails.getAuthorities().toArray()[0].toString());
        claims.put("USER_ID", userDetails.getId());
        claims.put("EMAIL", userDetails.getEmail());
        return claims;
    }

    @SneakyThrows
    private Key key() {
        byte[] bytes = Files.readAllBytes(Paths.get(jwtSecretKeyPath));
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUserNameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build() //build a JwtParser object to parse and verify token
                .parseClaimsJws(token) //the JwtParser object get a token to parse
                .getBody().get("USER_NAME").toString(); //get username String from payload
    }

    public String getSessionIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build() //build a JwtParser object to parse and verify token
                .parseClaimsJws(token) //the JwtParser object get a token to parse
                .getBody().get("SESSION_ID").toString(); //get sessionId String from payload
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build() //build a JwtParser object to parse and verify token
                .parseClaimsJws(token) //the JwtParser object get a token to parse
                .getBody().get("EMAIL").toString(); //get email String from payload
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token); //build a JwtParser object to verify token
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public boolean checkEmailInToken(String email, String token) {
        if (StringUtils.isNotBlank(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return StringUtils.equals(email, getEmailFromToken(token));
    }
}
