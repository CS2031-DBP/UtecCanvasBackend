package com.example.gestiondecursos.Evaluation.infrastructure;

import com.example.gestiondecursos.Evaluation.domain.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseEvaluationRepository <T extends Evaluation> extends JpaRepository<T, Long> {
}
