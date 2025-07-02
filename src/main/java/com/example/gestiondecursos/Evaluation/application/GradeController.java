package com.example.gestiondecursos.Evaluation.application;

import com.example.gestiondecursos.Evaluation.domain.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<GradeService.GradeDTO>> getStudentGrades(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<GradeService.GradeDTO> grades = gradeService.getStudentGrades(studentId, courseId);
        return ResponseEntity.ok(grades);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/course/{courseId}/all-students")
    public ResponseEntity<List<GradeService.StudentGradesDTO>> getAllStudentsGrades(
            @PathVariable Long courseId) {
        List<GradeService.StudentGradesDTO> allStudentsGrades = gradeService.getAllStudentsGrades(courseId);
        return ResponseEntity.ok(allStudentsGrades);
    }
} 