package com.web.ecommerce.util;

import com.web.ecommerce.configuration.security.SecurityUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class Util {
    public static Long getUserIdFromSecurityContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken){
            return (long) -1;
        }
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return user.getUserId();
    }
}
