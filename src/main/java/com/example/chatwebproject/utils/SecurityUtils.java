package com.example.chatwebproject.utils;

import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    public static final String CLAIMS_NAMESPACE = "https://www.jhipster.tech/";

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipal(securityContext.getAuthentication());
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        }
//        else if (authentication instanceof JwtAuthenticationToken) {
//            return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims()
//                    .get(SecurityUtilsEnum.PREFERRED_USERNAME.getName());
//        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
//            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
//            if (attributes.containsKey(SecurityUtilsEnum.PREFERRED_USERNAME.getName())) {
//                return (String) attributes.get(SecurityUtilsEnum.PREFERRED_USERNAME.getName());
//            }
//        } else if (authentication.getPrincipal() instanceof String) {
//            return (String) authentication.getPrincipal();
//        }
        return Constants.EMPTY_STRING;
    }

    public static String getCurrentEmailLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipalForEmail(securityContext.getAuthentication());
    }

    private static String extractPrincipalForEmail(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl springSecurityUser = (UserDetailsImpl) authentication.getPrincipal();
            return springSecurityUser.getEmail();
        }
//        else if (authentication instanceof JwtAuthenticationToken) {
//            return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims()
//                    .get(SecurityUtilsEnum.PREFERRED_USERNAME.getName());
//        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
//            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
//            if (attributes.containsKey(SecurityUtilsEnum.PREFERRED_USERNAME.getName())) {
//                return (String) attributes.get(SecurityUtilsEnum.PREFERRED_USERNAME.getName());
//            }
//        } else if (authentication.getPrincipal() instanceof String) {
//            return (String) authentication.getPrincipal();
//        }
        return Constants.EMPTY_STRING;
    }
}

