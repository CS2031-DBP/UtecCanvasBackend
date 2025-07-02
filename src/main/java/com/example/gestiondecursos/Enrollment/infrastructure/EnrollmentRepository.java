package com.example.gestiondecursos.Enrollment.infrastructure;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByCourseAndStudent(Course course, Student student);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByCourseId(Long courseId);
}
