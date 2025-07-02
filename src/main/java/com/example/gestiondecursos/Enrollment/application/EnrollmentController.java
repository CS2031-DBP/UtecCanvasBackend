package com.example.gestiondecursos.Enrollment.application;

import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Enrollment.domain.EnrollmentService;
import com.example.gestiondecursos.User.dto.UserResponseDTO;
import com.example.gestiondecursos.Enrollment.dto.EnrollmentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/courseId/{id}/studentEmail/{email}")
    public ResponseEntity<Void> createdEnrollment(@PathVariable Long id, @PathVariable String email){
        enrollmentService.createEnrollment(id, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<Enrollment> getById(@PathVariable Long id){
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(enrollment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/courseId/{courseId}/studentEmail/{email}")
    public ResponseEntity<Void> removeEnrollment(@PathVariable Long courseId, @PathVariable String email){
        enrollmentService.removeEnrollment(courseId, email);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<List<UserResponseDTO>> getStudentsByCourse(@PathVariable Long courseId) {
        List<UserResponseDTO> students = enrollmentService.getStudentsByCourse(courseId);
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allEnrollments")
    @ResponseBody
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getAllEnrollmentsAsDTO();
        return ResponseEntity.ok(enrollments);
    }
}
