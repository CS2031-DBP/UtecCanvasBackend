package com.example.gestiondecursos.Question.domain;

import com.example.gestiondecursos.Quiz.domain.QuizSubmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private QuizSubmission submission;

    @ManyToOne
    private Question question;

    private String answer;
}
