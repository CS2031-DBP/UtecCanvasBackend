package com.example.gestiondecursos.Assignment.application;

import com.example.gestiondecursos.Assignment.Dto.AssignmentGradeDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentSubmissionDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentSubmissionRequestDTO;
import com.example.gestiondecursos.Assignment.domain.AssignmentSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment-submission")
@RequiredArgsConstructor
public class AssignmentSubmissionController {
    private final AssignmentSubmissionService submissionService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/submit")
    public ResponseEntity<AssignmentSubmissionDTO> submitAssignment(@Valid @RequestBody AssignmentSubmissionRequestDTO requestDTO) {
        AssignmentSubmissionDTO submission = submissionService.submitAssignment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/{submissionId}/grade")
    public ResponseEntity<AssignmentSubmissionDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody AssignmentGradeDTO gradeDTO) {
        AssignmentSubmissionDTO submission = submissionService.gradeSubmission(submissionId, gradeDTO);
        return ResponseEntity.ok(submission);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<AssignmentSubmissionDTO>> getSubmissionsByAssignment(@PathVariable Long assignmentId) {
        List<AssignmentSubmissionDTO> submissions = submissionService.getSubmissionsByAssignment(assignmentId);
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AssignmentSubmissionDTO>> getStudentSubmissions(@PathVariable Long studentId) {
        List<AssignmentSubmissionDTO> submissions = submissionService.getStudentSubmissions(studentId);
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/assignment/{assignmentId}/submitted")
    public ResponseEntity<Boolean> hasStudentSubmittedAssignment(@PathVariable Long assignmentId) {
        boolean hasSubmitted = submissionService.hasStudentSubmittedAssignment(assignmentId);
        return ResponseEntity.ok(hasSubmitted);
    }
} 