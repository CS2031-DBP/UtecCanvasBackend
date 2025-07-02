package com.example.gestiondecursos.Material.application;

import com.example.gestiondecursos.Material.domain.Material;
import com.example.gestiondecursos.Material.domain.MaterialService;
import com.example.gestiondecursos.Material.dto.MaterialRequestDTO;
import com.example.gestiondecursos.Material.dto.MaterialResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialController {
    private final MaterialService materialService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courseId/{courseId}/week/{week}")
    public ResponseEntity<MaterialResponseDTO> createMaterial(@PathVariable Long courseId,@PathVariable Integer week, @RequestBody @Valid MaterialRequestDTO material1){
        MaterialResponseDTO material = materialService.createMaterial(courseId, week, material1);
        return ResponseEntity.status(HttpStatus.CREATED).body(material);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<MaterialResponseDTO>> getMaterialsByCourse(@PathVariable Long courseId) {
        List<MaterialResponseDTO> materials = materialService.getMaterialsByCourse(courseId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<MaterialResponseDTO>> getMaterialsByLesson(@PathVariable Long lessonId) {
        List<MaterialResponseDTO> materials = materialService.getMaterialsByLesson(lessonId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/test/lesson/{courseId}/{week}")
    public ResponseEntity<String> testLessonExists(@PathVariable Long courseId, @PathVariable Integer week) {
        try {
            materialService.testLessonExists(courseId, week);
            return ResponseEntity.ok("Lesson exists for course " + courseId + " and week " + week);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{materialId}")
    public ResponseEntity<MaterialResponseDTO> updateMaterial(@PathVariable Long materialId, @RequestBody @Valid MaterialRequestDTO materialRequest) {
        MaterialResponseDTO updatedMaterial = materialService.updateMaterial(materialId, materialRequest);
        return ResponseEntity.ok(updatedMaterial);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable Long materialId) {
        materialService.deleteMaterial(materialId);
        return ResponseEntity.noContent().build();
    }
}
