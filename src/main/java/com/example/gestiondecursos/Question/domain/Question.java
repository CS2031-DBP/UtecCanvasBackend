package com.example.gestiondecursos.Question.domain;


import com.example.gestiondecursos.Evaluation.domain.Evaluation;
import com.example.gestiondecursos.Quiz.domain.Quiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Table(name = "question_table")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Question{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = true)
    private String correctAnswer;

    @ElementCollection
    private List<String> options;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private Boolean isOpen = false;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}
