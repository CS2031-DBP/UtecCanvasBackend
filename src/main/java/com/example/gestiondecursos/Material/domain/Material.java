package com.example.gestiondecursos.Material.domain;

import com.example.gestiondecursos.Lesson.domain.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String type;

    private String url;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
