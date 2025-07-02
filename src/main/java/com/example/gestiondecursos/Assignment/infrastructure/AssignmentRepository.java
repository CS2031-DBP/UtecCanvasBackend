package com.example.gestiondecursos.Assignment.infrastructure;

import com.example.gestiondecursos.Assignment.domain.Assignment;
import com.example.gestiondecursos.Evaluation.infrastructure.BaseEvaluationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface AssignmentRepository extends BaseEvaluationRepository<Assignment> {
    Optional<Assignment> findByTitle(String title);
    List<Assignment> findByCourseId(Long courseId);
}
