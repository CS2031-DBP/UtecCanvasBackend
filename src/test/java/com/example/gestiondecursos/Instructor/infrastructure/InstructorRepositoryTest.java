package com.example.gestiondecursos.Instructor.infrastructure;

import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.PostgresTestContainerConfig;
import com.example.gestiondecursos.User.domain.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
public class InstructorRepositoryTest {

    @Autowired
    InstructorRepository instructorRepository;

    Instructor instructor1;
    Instructor instructor2;

    @BeforeEach
    void setUp() {
        instructorRepository.deleteAll();

        instructor1 = new Instructor();
        instructor1.setName("Luis");
        instructor1.setLastname("Mora");
        instructor1.setEmail("luis@example.com");
        instructor1.setPassword("12345");
        instructor1.setRole(Roles.INSTRUCTOR);

        instructor2 = new Instructor();
        instructor2.setName("Carla");
        instructor2.setLastname("Fernandez");
        instructor2.setEmail("carla@example.com");
        instructor2.setPassword("12345");
        instructor2.setRole(Roles.INSTRUCTOR);

        instructorRepository.save(instructor1);
        instructorRepository.save(instructor2);
    }

    @Test
    void findByEmail_shouldReturnInstructor() {
        Optional<Instructor> found = instructorRepository.findByEmail("luis@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Luis");
    }

    @Test
    void findByName_shouldReturnInstructors() {
        List<Instructor> found = instructorRepository.findByName("Luis");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("luis@example.com");
    }

    @Test
    void findByNameAndLastname_shouldReturnInstructor() {
        Optional<Instructor> found = instructorRepository.findByNameAndLastname("Luis", "Mora");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("luis@example.com");
    }
}
