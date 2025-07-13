package com.example.chatwebproject.security.jwt;


import com.example.chatwebproject.security.service.UserDetailImpl;
import com.example.chatwebproject.security.service.UserDetailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailServiceImpl userDetailsService;
    @Autowired
    private JwtProvider jwtProvider;
//    @Autowired
//    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            //get token from Header
            String jwt = parseJwt(request);
            if (jwt != null && jwtProvider.validateToken(jwt)) {
                //authority
                handleUserToken(jwt, request);
            }
        } catch (Exception e) {
            log.error("Can not set user authentication: " + e);
        }

        filterChain.doFilter(request, response);
        String uriTemplate = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (uriTemplate != null) {
            request.setAttribute("uriTemplate", uriTemplate);
        }

    }

    private void handleUserToken(String jwt, HttpServletRequest request) {
        String email = this.jwtProvider.getEmailFromToken(jwt);
        UserDetailImpl userDetails = (UserDetailImpl)this.userDetailsService.loadUserByUsername(email);

        //check sessionId trong redis
//        String sessionIdJwt = this.jwtProvider.getSessionIdFromToken(jwt);
//        String key = "userId:" + userDetails.getId();
//        String sessionIdRedis = this.redisUtil.getValue(key);
//        if (!org.apache.commons.lang.StringUtils.equals(sessionIdJwt, sessionIdRedis)) {
//            log.error("sessionId is invalid");
//            throw new IllegalArgumentException("sessionId is invalid");
//        }

        //Set authentication vào SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
