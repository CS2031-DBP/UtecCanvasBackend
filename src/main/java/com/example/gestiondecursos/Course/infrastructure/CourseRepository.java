package com.example.gestiondecursos.Course.infrastructure;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByTitle(String title);
    Optional<Course> findCourseByTitleAndCategory(String title, String category);
    Optional<Course> findCourseByTitleAndSection(String title, String section);
    List<Course> findByInstructor(Instructor instructor);
    boolean existsByIdAndInstructorId(Long courseId, Long instructorId);

}
