package com.example.chatwebproject.utils;

import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.security.service.UserDetailImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtil {

    public static final String CLAIMS_NAMESPACE = "https://www.jhipster.tech/";

    private SecurityUtil() {
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
        } else if (authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl springSecurityUser = (UserDetailImpl) authentication.getPrincipal();
            return springSecurityUser.getEmail();
        }
        return Constants.EMPTY_STRING;
    }


    public static Long getCurrentUserIdLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return extractPrincipalForUserId(securityContext.getAuthentication());
    }

    private static Long extractPrincipalForUserId(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetailImpl) {
            UserDetailImpl springSecurityUser = (UserDetailImpl) authentication.getPrincipal();
            return springSecurityUser.getId();
        }
        return null;
    }
}

