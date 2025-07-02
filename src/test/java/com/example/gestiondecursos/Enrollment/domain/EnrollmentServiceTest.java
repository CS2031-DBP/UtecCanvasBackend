package com.example.gestiondecursos.Enrollment.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void createEnrollment_success() {
        Course course = new Course();
        course.setId(1L);

        Student student = new Student();
        student.setEmail("student@example.com");

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        Mockito.when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));

        enrollmentService.createEnrollment(1L, "student@example.com");

        Mockito.verify(enrollmentRepository).save(Mockito.any(Enrollment.class));
    }

    @Test
    void getEnrollmentById_found() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);

        Mockito.when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment result = enrollmentService.getEnrollmentById(1L);

        assertThat(result).isEqualTo(enrollment);
    }

    @Test
    void removeEnrollment_success() {
        Course course = new Course();
        Student student = new Student();
        Enrollment enrollment = new Enrollment();

        Mockito.when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        Mockito.when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
        Mockito.when(enrollmentRepository.findByCourseAndStudent(course, student)).thenReturn(Optional.of(enrollment));

        enrollmentService.removeEnrollment(1L, "student@example.com");

        Mockito.verify(enrollmentRepository).delete(enrollment);
    }
}
