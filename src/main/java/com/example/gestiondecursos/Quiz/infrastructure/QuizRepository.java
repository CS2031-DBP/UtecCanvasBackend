package com.example.gestiondecursos.Quiz.infrastructure;

import com.example.gestiondecursos.Evaluation.infrastructure.BaseEvaluationRepository;
import com.example.gestiondecursos.Quiz.domain.Quiz;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface QuizRepository extends BaseEvaluationRepository<Quiz> {
    Optional<Quiz> findByTitle(String title);
    Optional<Quiz> findByTitleAndCourseId(String title, Long courseId);
    List<Quiz> findByCourseIdOrderByCreatedAtDesc(Long courseId);
}
