package com.example.gestiondecursos.Instructor.domain;

import com.example.gestiondecursos.Auth.utils.AuthorizationUtils;
import com.example.gestiondecursos.Instructor.dto.InstructorCreatedDTO;
import com.example.gestiondecursos.Instructor.dto.InstructorResponseDTO;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.User.domain.Roles;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    InstructorRepository instructorRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    AuthorizationUtils authorizationUtils;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    InstructorService instructorService;

    @Test
    void createInstructor_success() {
        InstructorCreatedDTO dto = new InstructorCreatedDTO();
        dto.setName("Ana");
        dto.setLastname("Lopez");
        dto.setEmail("ana@example.com");
        dto.setPassword("pass123");

        when(instructorRepository.findByEmail("ana@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        Instructor savedInstructor = new Instructor();
        savedInstructor.setId(1L);
        savedInstructor.setName("Ana");
        savedInstructor.setLastname("Lopez");
        savedInstructor.setEmail("ana@example.com");
        savedInstructor.setPassword("encodedPass");
        savedInstructor.setRole(Roles.INSTRUCTOR);

        when(instructorRepository.save(any(Instructor.class))).thenReturn(savedInstructor);

        InstructorResponseDTO responseDTO = new InstructorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Ana");
        responseDTO.setLastname("Lopez");
        responseDTO.setEmail("ana@example.com");

        when(modelMapper.map(any(Instructor.class), eq(InstructorResponseDTO.class))).thenReturn(responseDTO);

        InstructorResponseDTO result = instructorService.createInstructor(dto);

        assertNotNull(result);
        assertEquals("Ana", result.getName());
        assertEquals("ana@example.com", result.getEmail());

        verify(instructorRepository).save(any(Instructor.class));
    }

    @Test
    void createInstructor_emailExists_throwsException() {
        InstructorCreatedDTO dto = new InstructorCreatedDTO();
        dto.setEmail("exists@example.com");

        when(instructorRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(new Instructor()));

        assertThrows(UserAlreadyExistException.class, () -> {
            instructorService.createInstructor(dto);
        });
    }

    @Test
    void getInstructorInfo_success() {
        Instructor instructor = new Instructor();
        instructor.setId(1L);
        instructor.setName("Carlos");

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        InstructorResponseDTO responseDTO = new InstructorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Carlos");

        when(modelMapper.map(instructor, InstructorResponseDTO.class)).thenReturn(responseDTO);

        InstructorResponseDTO result = instructorService.getInstructorInfo(1L);

        assertNotNull(result);
        assertEquals("Carlos", result.getName());
    }

    @Test
    void getInstructorInfo_notFound_throwsException() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> {
            instructorService.getInstructorInfo(99L);
        });
    }


    @Test
    void updateInstructor_success() {
        Instructor instructor = new Instructor();
        instructor.setId(1L);
        instructor.setProfilePhoto("oldPhoto");

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        instructorService.updateInstructor(1L, "newPhoto");

        assertEquals("newPhoto", instructor.getProfilePhoto());
        verify(instructorRepository).save(instructor);
    }

    @Test
    void updateInstructor_notFound_throwsException() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> {
            instructorService.updateInstructor(99L, "photo");
        });
    }

    @Test
    void deleteInstructor_success() {
        Instructor instructor = new Instructor();
        instructor.setId(1L);

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        Mockito.doNothing().when(instructorRepository).delete(instructor);

        instructorService.deleteInstructor(1L);

        verify(instructorRepository).delete(instructor);
    }

    @Test
    void deleteInstructor_notFound_throwsException() {
        when(instructorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> {
            instructorService.deleteInstructor(99L);
        });
    }
}
