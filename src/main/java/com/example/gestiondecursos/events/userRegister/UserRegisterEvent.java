package com.example.gestiondecursos.events.userRegister;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisterEvent extends ApplicationEvent {
    private final String email;
    private final String name;
    private final String lastname;
    private final String password;

    public UserRegisterEvent(Object source,String name, String lastname,String email, String password){
        super(source);
        this.email = email;
        this.name = name;
        this.lastname = lastname;
        this.password = password;
    }
}
