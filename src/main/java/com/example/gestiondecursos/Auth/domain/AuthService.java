package com.example.gestiondecursos.Auth.domain;

import com.example.gestiondecursos.Auth.dto.JwtAuthResponse;
import com.example.gestiondecursos.Auth.dto.LoginDTO;
import com.example.gestiondecursos.Auth.dto.RegisterDTO;
import com.example.gestiondecursos.Config.JwtService;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.infrastructure.BaseUserRepository;
import com.example.gestiondecursos.events.userRegister.UserRegisterEvent;
import com.example.gestiondecursos.exceptions.PasswordIncorrectException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.gestiondecursos.User.domain.Roles.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BaseUserRepository<User> userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final InstructorService instructorService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public JwtAuthResponse login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new ResourceNotFound("User doesn't exist or incorrect email "));
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new PasswordIncorrectException("Your password is incorrect");
        }
        
        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwtService.generatedToken(user));
        
        // Incluir datos del usuario en la respuesta
        JwtAuthResponse.UserInfo userInfo = new JwtAuthResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setEmail(user.getEmail());
        userInfo.setName(user.getName());
        userInfo.setLastname(user.getLastname());
        userInfo.setRole(user.getRole().name());
        response.setUser(userInfo);
        
        return response;
    }

    public JwtAuthResponse register(RegisterDTO registerDTO){
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email already registered");
        }

        if("INSTRUCTOR".equals(registerDTO.getRole())){
            Instructor instructor = new Instructor();
            instructor.setName(registerDTO.getName());
            instructor.setLastname(registerDTO.getLastname());
            instructor.setEmail(registerDTO.getEmail());
            instructor.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            instructor.setRole(INSTRUCTOR);
            userRepository.save(instructor);
            applicationEventPublisher.publishEvent(new UserRegisterEvent(this, registerDTO.getName(), registerDTO.getLastname(), registerDTO.getEmail(), registerDTO.getPassword()));
            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(jwtService.generatedToken(instructor));
            
            // Incluir datos del instructor en la respuesta
            JwtAuthResponse.UserInfo userInfo = new JwtAuthResponse.UserInfo();
            userInfo.setId(instructor.getId());
            userInfo.setEmail(instructor.getEmail());
            userInfo.setName(instructor.getName());
            userInfo.setLastname(instructor.getLastname());
            userInfo.setRole(instructor.getRole().name());
            response.setUser(userInfo);
            
            return response;
        }
        else if ("STUDENT".equals(registerDTO.getRole())) {
            Student student = new Student();
            student.setName(registerDTO.getName());
            student.setLastname(registerDTO.getLastname());
            student.setEmail(registerDTO.getEmail());
            student.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            student.setRole(STUDENT);
            userRepository.save(student);
            applicationEventPublisher.publishEvent(new UserRegisterEvent(this, registerDTO.getName(), registerDTO.getLastname(), registerDTO.getEmail(), registerDTO.getPassword()));
            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(jwtService.generatedToken(student));
            
            // Incluir datos del estudiante en la respuesta
            JwtAuthResponse.UserInfo userInfo = new JwtAuthResponse.UserInfo();
            userInfo.setId(student.getId());
            userInfo.setEmail(student.getEmail());
            userInfo.setName(student.getName());
            userInfo.setLastname(student.getLastname());
            userInfo.setRole(student.getRole().name());
            response.setUser(userInfo);
            
            return response;
        }
        else if ("ADMIN".equals(registerDTO.getRole())) {
            User user1 = new User();
            user1.setName(registerDTO.getName());
            user1.setLastname(registerDTO.getLastname());
            user1.setEmail(registerDTO.getEmail());
            user1.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            user1.setRole(ADMIN);
            userRepository.save(user1);
            applicationEventPublisher.publishEvent(new UserRegisterEvent(this, registerDTO.getName(), registerDTO.getLastname(), registerDTO.getEmail(), registerDTO.getPassword()));
            JwtAuthResponse response = new JwtAuthResponse();
            response.setToken(jwtService.generatedToken(user1));
            
            // Incluir datos del admin en la respuesta
            JwtAuthResponse.UserInfo userInfo = new JwtAuthResponse.UserInfo();
            userInfo.setId(user1.getId());
            userInfo.setEmail(user1.getEmail());
            userInfo.setName(user1.getName());
            userInfo.setLastname(user1.getLastname());
            userInfo.setRole(user1.getRole().name());
            response.setUser(userInfo);
            
            return response;
        }
        else {
            throw new IllegalArgumentException("Invalid role: " + registerDTO.getRole());
        }
    }
}
