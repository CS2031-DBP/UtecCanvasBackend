package com.example.gestiondecursos.Student.infrastructure;

import com.example.gestiondecursos.PostgresTestContainerConfig;
import com.example.gestiondecursos.Student.domain.Student;

import com.example.gestiondecursos.User.domain.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import(PostgresTestContainerConfig.class)
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        student1 = new Student();
        student1.setName("Juan");
        student1.setLastname("Perez");
        student1.setEmail("juan@example.com");
        student1.setPassword("12345");
        student1.setRole(Roles.STUDENT);

        student2 = new Student();
        student2.setName("Maria");
        student2.setLastname("Gomez");
        student2.setEmail("maria@example.com");
        student2.setPassword("12345");
        student2.setRole(Roles.STUDENT);

        studentRepository.save(student1);
        studentRepository.save(student2);
    }

    @Test
    void findByEmail_shouldReturnStudent() {
        Optional<Student> found = studentRepository.findByEmail("juan@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Juan");
    }

    @Test
    void findByName_shouldReturnStudents() {
        List<Student> found = studentRepository.findByName("Juan");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    void findByLastname_shouldReturnStudents() {
        List<Student> found = studentRepository.findByLastname("Gomez");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("maria@example.com");
    }

    @Test
    void findByNameAndLastname_shouldReturnStudent() {
        Optional<Student> found = studentRepository.findByNameAndLastname("Juan", "Perez");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("juan@example.com");
    }
}