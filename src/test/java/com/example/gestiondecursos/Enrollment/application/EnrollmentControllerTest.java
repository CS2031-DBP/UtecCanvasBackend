package com.example.gestiondecursos.Enrollment.application;

import com.example.gestiondecursos.CommonMockConfig;
import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Enrollment.domain.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({CommonMockConfig.class})
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createEnrollment_success() throws Exception {
        mockMvc.perform(post("/enrollment/courseId/1/studentEmail/student@example.com")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(enrollmentService).createEnrollment(1L, "student@example.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEnrollmentById_success() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);
        Mockito.when(enrollmentService.getEnrollmentById(1L)).thenReturn(enrollment);

        mockMvc.perform(get("/enrollment/getById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeEnrollment_success() throws Exception {
        mockMvc.perform(delete("/enrollment/courseId/1/studentEmail/student@example.com")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(enrollmentService).removeEnrollment(1L, "student@example.com");
    }
}
