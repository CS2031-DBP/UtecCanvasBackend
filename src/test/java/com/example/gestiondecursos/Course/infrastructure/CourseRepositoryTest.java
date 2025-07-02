package com.example.gestiondecursos.Course.infrastructure;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.PostgresTestContainerConfig;
import com.example.gestiondecursos.User.domain.Roles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    private Instructor instructor;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        instructorRepository.deleteAll();

        instructor = new Instructor();
        instructor.setName("Carlos");
        instructor.setLastname("Lopez");
        instructor.setEmail("carlos@example.com");
        instructor.setPassword("12345");
        instructor.setRole(Roles.INSTRUCTOR);
        instructor = instructorRepository.save(instructor);

        course1 = new Course();
        course1.setTitle("Matemáticas");
        course1.setDescription("Curso de Matemáticas básicas");
        course1.setSection("A");
        course1.setCategory("Teoría");
        course1.setInstructor(instructor);

        course2 = new Course();
        course2.setTitle("Física");
        course2.setDescription("Curso de Física avanzada");
        course2.setSection("B");
        course2.setCategory("Laboratorio");
        course2.setInstructor(instructor);

        courseRepository.save(course1);
        courseRepository.save(course2);
    }

    @Test
    void findAllByTitle_shouldReturnCourses() {
        List<Course> found = courseRepository.findAllByTitle("Matemáticas");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getDescription()).isEqualTo("Curso de Matemáticas básicas");
        assertThat(found.get(0).getInstructor().getEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    void findCourseByTitleAndCategory_shouldReturnCourse() {
        Optional<Course> found = courseRepository.findCourseByTitleAndCategory("Física", "Laboratorio");
        assertThat(found).isPresent();
        assertThat(found.get().getSection()).isEqualTo("B");
    }

    @Test
    void findCourseByTitleAndSection_shouldReturnCourse() {
        Optional<Course> found = courseRepository.findCourseByTitleAndSection("Matemáticas", "A");
        assertThat(found).isPresent();
        assertThat(found.get().getCategory()).isEqualTo("Teoría");
    }

    @Test
    void existsByIdAndInstructorId_shouldReturnTrue() {
        boolean exists = courseRepository.existsByIdAndInstructorId(course1.getId(), instructor.getId());
        assertThat(exists).isTrue();
    }
}
