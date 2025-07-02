package com.example.gestiondecursos.Material.application;


import com.example.gestiondecursos.CommonMockConfig;
import com.example.gestiondecursos.Material.dto.MaterialRequestDTO;
import com.example.gestiondecursos.Material.dto.MaterialResponseDTO;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.example.gestiondecursos.Material.domain.MaterialService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({CommonMockConfig.class})
public class MaterialControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaterialService materialService;

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void createMaterial_success() throws Exception {
        MaterialRequestDTO requestDTO = new MaterialRequestDTO();
        requestDTO.setTitle("Material 1");
        requestDTO.setType("Video");
        requestDTO.setUrl("http://example.com/video");

        MaterialResponseDTO responseDTO = new MaterialResponseDTO();
        responseDTO.setTitle("Material 1");
        responseDTO.setType("Video");
        responseDTO.setUrl("http://example.com/video");

        Mockito.when(materialService.createMaterial(anyLong(), anyInt(), any(MaterialRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/material/courseId/1/week/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Material 1"))
                .andExpect(jsonPath("$.type").value("Video"))
                .andExpect(jsonPath("$.url").value("http://example.com/video"));

        Mockito.verify(materialService).createMaterial(1L, 3, requestDTO);
    }
}
