package com.example.gestiondecursos.User.domain;

import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.User.dto.UserResponseDTO;
import com.example.gestiondecursos.User.dto.UpdateProfilePhotoDTO;
import com.example.gestiondecursos.User.infrastructure.BaseUserRepository;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BaseUserRepository<User> userRepository;
    private final ModelMapper modelMapper;

    public User getByEmail(String email, String role) {
        if (!"ROLE_ADMIN".equals(role)) {
        throw new AccessDeniedException("Only admins are allowed to access this method");
        }
    return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFound("User not found"));
    }

    public UserResponseDTO getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceIsNullException("User not authenticated");
        }

        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new ResourceIsNullException("Role not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UserResponseDTO updateProfilePhoto(UpdateProfilePhotoDTO updateDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceIsNullException("User not authenticated");
        }

        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        // Actualizar la foto de perfil
        user.setProfilePhoto(updateDTO.getProfilePhoto());
        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponseDTO.class);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        userRepository.delete(user);
    }

    public User getAuthenticatedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFound("User not found"));
    }

    @Bean(name = "UserDetailsService")
    public UserDetailsService userDetailsService(){
        return username -> {
            User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFound("User not found"));
            return (UserDetails) user;
        };
    }
}
