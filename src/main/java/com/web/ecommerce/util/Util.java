package com.web.ecommerce.util;

import com.web.ecommerce.configuration.security.SecurityUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Util {
    public static Long getUserIdFromSecurityContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken){
            return (long) -1;
        }
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return user.getUserId();
    }

    public static LocalDateTime getTokenExpirationTime(int expirationTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,expirationTime);
        return LocalDateTime.ofInstant(calendar.toInstant(),calendar.getTimeZone().toZoneId());
    }
}
