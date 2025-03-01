package com.example.chatwebproject.utils;

import com.example.chatwebproject.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class SessionIDUtils {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtProvider jwtProvider;

    public Long getUserIdFromAccessToken() {
        var jwtToken = request.getHeader(AUTHORIZATION_HEADER);
        if (jwtToken.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }
        if (StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            return this.jwtProvider.getUserIdFromToken(jwtToken);
        }
        return null;
    }
}
