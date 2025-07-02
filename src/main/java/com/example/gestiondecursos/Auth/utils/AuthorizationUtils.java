package com.example.gestiondecursos.Auth.utils;

import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationUtils {

    public String getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }catch (ClassCastException e){
            throw new ResourceIsNullException("User not found");
        }
    }
}
