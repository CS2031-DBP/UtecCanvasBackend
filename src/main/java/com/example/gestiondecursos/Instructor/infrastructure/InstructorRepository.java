package com.example.gestiondecursos.Instructor.infrastructure;

import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.User.infrastructure.BaseUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface InstructorRepository extends BaseUserRepository<Instructor> {
    List<Instructor> findByName(String name);
    Optional<Instructor> findByNameAndLastname(String name, String lastname);
    Optional<Instructor> findByEmail(String email);
}
