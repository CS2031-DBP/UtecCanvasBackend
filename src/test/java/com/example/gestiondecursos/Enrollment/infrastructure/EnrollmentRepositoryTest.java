package com.example.gestiondecursos.Enrollment.infrastructure;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.PostgresTestContainerConfig;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.User.domain.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
public class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    private Student student;
    private Instructor instructor;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        studentRepository.deleteAll();
        instructorRepository.deleteAll();

        instructor = new Instructor();
        instructor.setName("Profesor");
        instructor.setLastname("Uno");
        instructor.setEmail("profesor1@uni.edu");
        instructor.setPassword("pass");
        instructor.setRole(Roles.INSTRUCTOR);
        instructor = instructorRepository.save(instructor);

        course = new Course();
        course.setTitle("Curso 1");
        course.setDescription("Descripción del curso");
        course.setSection("A");
        course.setCategory("Teoría");
        course.setInstructor(instructor);
        course = courseRepository.save(course);

        student = new Student();
        student.setName("Estudiante");
        student.setLastname("Uno");
        student.setEmail("estudiante1@uni.edu");
        student.setPassword("pass");
        student.setRole(Roles.STUDENT);
        student = studentRepository.save(student);

        enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrolled(LocalDateTime.now());
        enrollment = enrollmentRepository.save(enrollment);
    }

    @Test
    void findByCourseAndStudent_shouldReturnEnrollment() {
        Optional<Enrollment> found = enrollmentRepository.findByCourseAndStudent(course, student);
        assertThat(found).isPresent();
        assertThat(found.get().getCourse().getTitle()).isEqualTo("Curso 1");
        assertThat(found.get().getStudent().getEmail()).isEqualTo("estudiante1@uni.edu");
    }

    @Test
    void existsByStudentIdAndCourseId_shouldReturnTrue() {
        boolean exists = enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), course.getId());
        assertThat(exists).isTrue();
    }
}
