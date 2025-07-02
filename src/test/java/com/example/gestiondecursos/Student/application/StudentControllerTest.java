package com.example.gestiondecursos.Student.application;

import com.example.gestiondecursos.CommonMockConfig;
import com.example.gestiondecursos.Student.Dto.StudentCreateDTO;
import com.example.gestiondecursos.Student.Dto.StudentRequestDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseForMeDTO;
import com.example.gestiondecursos.Student.domain.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(CommonMockConfig.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentResponseDTO makeStudentDTO() {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setEmail("juan@example.com");
        dto.setName("Juan");
        dto.setLastname("Perez");
        return dto;
    }

    private StudentResponseForMeDTO makeStudentForMeDTO() {
        StudentResponseForMeDTO dto = new StudentResponseForMeDTO();
        dto.setEmail("juan@example.com");
        dto.setName("Juan");
        dto.setLastname("Perez");
        dto.setDescription("Descripción");
        dto.setProfilePhoto("urlFoto");
        return dto;
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getStudentByEmail_success() throws Exception {
        StudentResponseDTO dto = makeStudentDTO();
        when(studentService.getStudentByEmail(anyString())).thenReturn(dto);

        mockMvc.perform(get("/student/getByEmail/juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.name").value(dto.getName()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getStudentByName_success() throws Exception {
        StudentResponseDTO dto = makeStudentDTO();
        when(studentService.getStudentsByName(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/student/getByName/Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dto.getName()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getStudentByLastname_success() throws Exception {
        StudentResponseDTO dto = makeStudentDTO();
        when(studentService.getStudentsByLastname(anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/student/getByLastname/Perez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastname").value(dto.getLastname()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getByFullName_success() throws Exception {
        StudentResponseDTO dto = makeStudentDTO();
        when(studentService.getByFullName(anyString(), anyString())).thenReturn(dto);

        mockMvc.perform(get("/student/getByFullName/name/Juan/lastname/Perez"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudent_success() throws Exception {
        Mockito.doNothing().when(studentService).deleteStudent(anyLong());

        mockMvc.perform(delete("/student/getById/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(studentService).deleteStudent(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStudent_success() throws Exception {
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setName("Nuevo Nombre");
        requestDTO.setLastname("Nuevo Apellido");

        Mockito.when(studentService.updateStudent(anyString(), any(StudentRequestDTO.class)))
                .thenReturn(makeStudentDTO());

        mockMvc.perform(patch("/student/update/email@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(studentService).updateStudent(eq("email@example.com"), any(StudentRequestDTO.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_success() throws Exception {
        StudentCreateDTO createDTO = new StudentCreateDTO();
        createDTO.setName("Juan");
        createDTO.setLastname("Perez");
        createDTO.setEmail("juan@example.com");
        createDTO.setPassword("123456");

        Mockito.when(studentService.createStudent(any(StudentCreateDTO.class))).thenReturn(makeStudentDTO());

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        Mockito.verify(studentService).createStudent(any(StudentCreateDTO.class));
    }


    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllStudents_success() throws Exception {
        StudentResponseDTO dto = makeStudentDTO();
        Mockito.when(studentService.getAllStudents()).thenReturn(List.of(dto));

        mockMvc.perform(get("/student/getAllStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(dto.getEmail()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getMyOwnInfo_success() throws Exception {
        StudentResponseForMeDTO dto = makeStudentForMeDTO();
        Mockito.when(studentService.getMyOwnInfo()).thenReturn(dto);

        mockMvc.perform(get("/student/getMyOwnInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.description").value(dto.getDescription()))
                .andExpect(jsonPath("$.profilePhoto").value(dto.getProfilePhoto()));
    }
}