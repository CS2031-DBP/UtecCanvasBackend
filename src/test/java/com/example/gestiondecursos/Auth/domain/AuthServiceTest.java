package com.example.gestiondecursos.Auth.domain;

import com.example.gestiondecursos.Auth.dto.JwtAuthResponse;
import com.example.gestiondecursos.Auth.dto.LoginDTO;
import com.example.gestiondecursos.Auth.dto.RegisterDTO;
import com.example.gestiondecursos.Config.JwtService;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.infrastructure.BaseUserRepository;
import com.example.gestiondecursos.events.userRegister.UserRegisterEvent;
import com.example.gestiondecursos.exceptions.PasswordIncorrectException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    BaseUserRepository<User> userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    ModelMapper modelMapper;

    @Mock
    InstructorService instructorService;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    AuthService authService;

    @Test
    void loginSuccess() {
        String email = "test@uni.edu";
        String rawPassword = "pass123";
        String encodedPassword = "encodedPass";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generatedToken(user)).thenReturn("jwt-token");

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(rawPassword);

        JwtAuthResponse response = authService.login(loginDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("unknown@uni.edu");
        loginDTO.setPassword("pass");

        assertThrows(ResourceNotFound.class, () -> authService.login(loginDTO));
    }

    @Test
    void loginPasswordIncorrect() {
        User user = new User();
        user.setEmail("test@uni.edu");
        user.setPassword("encodedPass");

        when(userRepository.findByEmail("test@uni.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encodedPass"))).thenReturn(false);

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@uni.edu");
        loginDTO.setPassword("wrongPass");

        assertThrows(PasswordIncorrectException.class, () -> authService.login(loginDTO));
    }

    @Test
    void registerSuccess() {
        String email = "newadmin@uni.edu";
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail(email);
        registerDTO.setName("New");
        registerDTO.setLastname("Admin");
        registerDTO.setPassword("pass123");
        registerDTO.setRole("ADMIN");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        }).when(userRepository).save(any(User.class));

        when(jwtService.generatedToken(any(User.class))).thenReturn("jwt-token");

        JwtAuthResponse response = authService.register(registerDTO);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(applicationEventPublisher, times(1)).publishEvent(any(UserRegisterEvent.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerEmailAlreadyExists() {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setEmail("exists@uni.edu");
        when(userRepository.findByEmail("exists@uni.edu")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> authService.register(registerDTO));
    }
}