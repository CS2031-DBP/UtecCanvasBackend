package com.example.gestiondecursos.Student.infrastructure;

import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.infrastructure.BaseUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface StudentRepository extends BaseUserRepository<Student> {
    Optional<Student> findByEmail(String email);
    List<Student> findByName(String name);
    List<Student> findByLastname(String lastname);
    Optional<Student> findByNameAndLastname(String name, String lastname);

}
