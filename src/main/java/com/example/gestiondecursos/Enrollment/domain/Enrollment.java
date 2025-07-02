package com.example.gestiondecursos.Enrollment.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Student.domain.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "Enrollment_table")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime enrolled;

//    private Integer maxScore;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
