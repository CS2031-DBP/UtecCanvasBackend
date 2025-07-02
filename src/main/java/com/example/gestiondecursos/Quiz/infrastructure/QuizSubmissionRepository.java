package com.example.gestiondecursos.Quiz.infrastructure;

import com.example.gestiondecursos.Quiz.domain.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizIdOrderBySubmittedAtDesc(Long quizId);
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    Optional<QuizSubmission> findByStudentIdAndQuizId(Long studentId, Long quizId);
    List<QuizSubmission> findByStudentId(Long studentId);
}
