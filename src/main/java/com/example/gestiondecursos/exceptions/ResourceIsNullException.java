package com.example.gestiondecursos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceIsNullException extends RuntimeException{
    public ResourceIsNullException(String message){
        super(message);
    }
}
