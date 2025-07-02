package com.example.gestiondecursos.Lesson.infrastructure;

import com.example.gestiondecursos.Lesson.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>{
    Optional<Lesson> findByTitle(String title);
    Optional<Lesson> findByWeek(Integer week);
    Optional<Lesson> findByCourseIdAndTitle(Long id, String title); //esto
    Optional<Lesson> findByCourseIdAndWeek(Long id, Integer week); // y esto se puede?
    List<Lesson> findByCourseIdOrderByWeekAsc(Long courseId);
}
