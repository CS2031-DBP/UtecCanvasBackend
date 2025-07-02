package com.example.gestiondecursos.Material.infrastructure;

import com.example.gestiondecursos.Material.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByLessonCourseId(Long courseId);
    List<Material> findByLessonId(Long lessonId);
}
