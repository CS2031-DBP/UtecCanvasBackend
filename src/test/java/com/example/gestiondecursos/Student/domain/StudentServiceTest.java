package com.example.gestiondecursos.Student.domain;

import com.example.gestiondecursos.Auth.utils.AuthorizationUtils;
import com.example.gestiondecursos.Student.Dto.StudentCreateDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseDTO;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.User.domain.Roles;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthorizationUtils authorizationUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    @Test
    void createStudent_success() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setName("Juan");
        dto.setLastname("Perez");
        dto.setEmail("juan@example.com");
        dto.setPassword("123456");

        when(studentRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encodedPass");

        Student studentSaved = new Student();
        studentSaved.setId(1L);
        studentSaved.setName("Juan");
        studentSaved.setLastname("Perez");
        studentSaved.setEmail("juan@example.com");
        studentSaved.setPassword("encodedPass");
        studentSaved.setRole(Roles.STUDENT);

        when(studentRepository.save(any(Student.class))).thenReturn(studentSaved);

        StudentResponseDTO responseDTO = new StudentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Juan");
        responseDTO.setLastname("Perez");
        responseDTO.setEmail("juan@example.com");

        when(modelMapper.map(any(Student.class), eq(StudentResponseDTO.class))).thenReturn(responseDTO);

        StudentResponseDTO result = studentService.createStudent(dto);

        assertNotNull(result);
        assertEquals("Juan", result.getName());
        assertEquals("juan@example.com", result.getEmail());

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_emailExists_throwsException() {
        StudentCreateDTO dto = new StudentCreateDTO();
        dto.setEmail("exists@example.com");

        when(studentRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(new Student()));

        assertThrows(UserAlreadyExistException.class, () -> {
            studentService.createStudent(dto);
        });
    }
}
