package com.example.gestiondecursos.Assignment.domain;

import com.example.gestiondecursos.Evaluation.domain.Evaluation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Assignment extends Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String material;

    @Column(nullable = false)
    private Boolean uploadRequired;
}
