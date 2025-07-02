package com.example.gestiondecursos.Lesson.application;

import com.example.gestiondecursos.Lesson.domain.Lesson;
import com.example.gestiondecursos.Lesson.domain.LessonService;
import com.example.gestiondecursos.Lesson.dto.LessonRequestDTO;
import com.example.gestiondecursos.Lesson.dto.LessonResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lesson")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courseId/{id}")
    public ResponseEntity<Void> createdLesson(@PathVariable Long id,@RequestBody @Valid LessonRequestDTO lesson){
        lessonService.createLesson(id, lesson);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponseDTO>> getLessonsByCourse(@PathVariable Long courseId){
        List<LessonResponseDTO> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courseId/{courseId}/title/{title}")
    public ResponseEntity<LessonResponseDTO> getLessonByTitle(@PathVariable Long courseId,@PathVariable String title){
        LessonResponseDTO lesson = lessonService.getLessonByTitle(courseId, title);
        return ResponseEntity.status(HttpStatus.OK).body(lesson);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courseId/{courseId}/getByWeek/{week}")
    public ResponseEntity<LessonResponseDTO> getLessonByWeek(@PathVariable Long courseId,@PathVariable Integer week){
        LessonResponseDTO lesson = lessonService.getLessonByWeek(courseId, week);
        return ResponseEntity.status(HttpStatus.OK).body(lesson);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id){
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PatchMapping("/lessonId/{id}")
    public ResponseEntity<Void> updateLesson(@PathVariable Long id, @RequestBody @Valid LessonRequestDTO lesson){
        lessonService.updateLesson(id, lesson);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test/{courseId}")
    public ResponseEntity<String> testLessonsWithMaterials(@PathVariable Long courseId){
        try {
            List<LessonResponseDTO> lessons = lessonService.getLessonsByCourse(courseId);
            StringBuilder result = new StringBuilder();
            result.append("Lessons for course ").append(courseId).append(":\n");
            for (LessonResponseDTO lesson : lessons) {
                result.append("- Lesson: ").append(lesson.getTitle()).append(" (Week ").append(lesson.getWeek()).append(")\n");
                if (lesson.getMaterials() != null) {
                    result.append("  Materials: ").append(lesson.getMaterials().size()).append("\n");
                    for (var material : lesson.getMaterials()) {
                        result.append("    - ").append(material.getTitle()).append(" (").append(material.getType()).append(")\n");
                    }
                } else {
                    result.append("  Materials: null\n");
                }
            }
            return ResponseEntity.ok(result.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
