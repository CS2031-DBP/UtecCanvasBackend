package com.example.gestiondecursos.Quiz.domain;

import com.example.gestiondecursos.Question.domain.QuestionAnswer;
import com.example.gestiondecursos.Student.domain.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Quiz quiz;

    private Double automaticScore; // Score for multiple choice questions
    private Double manualScore; // Score for open questions (set by instructor)
    private Double finalScore; // Final score (set by instructor)
    private String status; // "SUBMITTED", "GRADED"

    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<QuestionAnswer> answers = new ArrayList<>();
}
