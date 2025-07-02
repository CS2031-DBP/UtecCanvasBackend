package com.example.gestiondecursos.Assignment.application;

import com.example.gestiondecursos.Assignment.Dto.AssignmentRequestDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentResponseDTO;
import com.example.gestiondecursos.Assignment.domain.Assignment;
import com.example.gestiondecursos.Assignment.domain.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignmentsByCourse(@PathVariable Long courseId) {
        List<AssignmentResponseDTO> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return ResponseEntity.ok(assignments);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courseId/{id}")
    public ResponseEntity<AssignmentResponseDTO> createAssignment(@PathVariable Long id,@RequestBody @Valid AssignmentRequestDTO assignment){
        AssignmentResponseDTO assignment1 = assignmentService.createAssignment(id, assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment1);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PatchMapping("/assignmentId/{id}")
    public ResponseEntity<Void> updateAssignment(@PathVariable Long id,@RequestBody @Valid AssignmentRequestDTO assignment){
        assignmentService.updateAssignment(id, assignment);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/deleteAssignment/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id){
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/getByTitle/{title}")
    public ResponseEntity<AssignmentResponseDTO> getByTitle(@PathVariable String title){
        AssignmentResponseDTO assignment = assignmentService.getByTitle(title);
        return ResponseEntity.status(HttpStatus.OK).body(assignment);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponseDTO> getAssignmentById(@PathVariable Long id){
        AssignmentResponseDTO assignment = assignmentService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }
}
