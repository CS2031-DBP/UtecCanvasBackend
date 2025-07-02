package com.example.gestiondecursos.Course.application;

import com.example.gestiondecursos.CommonMockConfig;
import com.example.gestiondecursos.Course.domain.CourseService;
import com.example.gestiondecursos.Course.dto.CourseRequestDTO;
import com.example.gestiondecursos.Course.dto.CourseRequestForUpdateDTO;
import com.example.gestiondecursos.Course.dto.CourseResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(CourseController.class)
@Import({CommonMockConfig.class})
@AutoConfigureMockMvc(addFilters = true)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getById_success() throws Exception {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setTitle("Matemáticas");
        dto.setInstructorName("Juan");
        dto.setInstructorLastname("Perez");

        Mockito.when(courseService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/course/getById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Matemáticas"))
                .andExpect(jsonPath("$.instructorName").value("Juan"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCourse_success() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setTitle("Fisica");
        request.setDescription("Descripcion");
        request.setSection("A1");
        request.setCategory("Laboratorio");

        mockMvc.perform(post("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(courseService).createCourse(any(CourseRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_success() throws Exception {
        CourseRequestForUpdateDTO updateDTO = new CourseRequestForUpdateDTO();
        updateDTO.setTitle("New Title");

        mockMvc.perform(patch("/course/courseId/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(courseService).updateCourse(eq(1L), any(CourseRequestForUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllByTitle_success() throws Exception {
        List<CourseResponseDTO> list = List.of(new CourseResponseDTO() {{
            setTitle("Programación");
            setInstructorName("Ana");
            setInstructorLastname("Lopez");
        }});

        Mockito.when(courseService.getAllByTitle("Programación")).thenReturn(list);

        mockMvc.perform(get("/course/getByTitle/Programación"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Programación"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_success() throws Exception {
        Mockito.doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/course/courseId/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(courseService).deleteCourse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignInstructor_success() throws Exception {
        Mockito.doNothing().when(courseService).assignInstructor("inst@uni.edu", 1L);

        mockMvc.perform(post("/course/instructor/inst@uni.edu/courseId/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(courseService).assignInstructor("inst@uni.edu", 1L);
    }
}
