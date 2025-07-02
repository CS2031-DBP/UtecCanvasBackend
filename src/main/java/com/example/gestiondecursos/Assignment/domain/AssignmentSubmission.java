package com.example.gestiondecursos.Assignment.domain;

import com.example.gestiondecursos.Student.domain.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assignment_submission_table")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    private String submissionText; // Texto de la entrega
    private String fileUrl; // URL del archivo subido (si aplica)
    private Double score; // Puntuación asignada por el instructor
    private String feedback; // Comentarios del instructor
    private String status; // "SUBMITTED", "GRADED", "LATE"

    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
} 