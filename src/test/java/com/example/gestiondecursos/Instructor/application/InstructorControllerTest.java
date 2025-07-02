package com.example.gestiondecursos.Instructor.application;

import com.example.gestiondecursos.CommonMockConfig;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Instructor.dto.InstructorCreatedDTO;
import com.example.gestiondecursos.Instructor.dto.InstructorResponseDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(InstructorController.class)
@Import({CommonMockConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class InstructorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createInstructor_success() throws Exception {
        InstructorCreatedDTO dto = new InstructorCreatedDTO();
        dto.setName("Ana");
        dto.setLastname("Lopez");
        dto.setEmail("ana@example.com");
        dto.setPassword("123456");

        InstructorResponseDTO responseDTO = new InstructorResponseDTO();
        responseDTO.setName("Ana");
        responseDTO.setLastname("Lopez");
        responseDTO.setEmail("ana@example.com");

        Mockito.when(instructorService.createInstructor(any(InstructorCreatedDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/instructor/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getInstructorInfo_success() throws Exception {
        InstructorResponseDTO dto = new InstructorResponseDTO();
        dto.setName("Ana");
        dto.setLastname("Lopez");
        dto.setEmail("ana@example.com");

        Mockito.when(instructorService.getInstructorOwnInfo()).thenReturn(dto);

        mockMvc.perform(get("/instructor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllInstructors_success() throws Exception {
        List<InstructorResponseDTO> list = List.of(new InstructorResponseDTO(){{
            setName("Ana");
            setLastname("Lopez");
            setEmail("ana@example.com");
        }});

        Mockito.when(instructorService.getAllInstructors()).thenReturn(list);

        mockMvc.perform(get("/instructor/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@example.com"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteInstructor_success() throws Exception {
        Mockito.doNothing().when(instructorService).deleteInstructor(1L);

        mockMvc.perform(delete("/instructor/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(instructorService).deleteInstructor(1L);
    }
}
