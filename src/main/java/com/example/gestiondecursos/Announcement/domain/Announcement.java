package com.example.gestiondecursos.Announcement.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String message;

    private String instructorName;
    private String instructorLastname;

    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
