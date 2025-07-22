package com.example.gestiondecursos.Enrollment.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.dto.CourseResponseDTO;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.domain.StudentService;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.dto.UserResponseDTO;
import java.util.stream.Collectors;
import com.example.gestiondecursos.Course.domain.CourseService;
import org.modelmapper.ModelMapper;
import com.example.gestiondecursos.Enrollment.dto.EnrollmentResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final CourseService courseService;
    private final ModelMapper modelMapper;
    private final InstructorService instructorService;
    private final StudentService studentService;

    public void createEnrollment(Long courseId, String studentEmail){
        Enrollment enrollment = new Enrollment();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFound("Course not found"));
        Student student = studentRepository.findByEmail(studentEmail).orElseThrow(() -> new ResourceNotFound("Student not found"));
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrolled(LocalDateTime.now());
//        enrollment.setMaxScore(0);
        enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id){
        Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Enrollment not found"));
        return enrollment;
    }

    public void removeEnrollment(Long courseId, String studentEmail){
        //Enrollment enrollment = enrollmentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Enrollment not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFound("Course not found"));
        Student student = studentRepository.findByEmail(studentEmail).orElseThrow(() -> new ResourceNotFound("Student not found"));
        Enrollment enrollment = enrollmentRepository.findByCourseAndStudent(course, student).orElseThrow(() -> new ResourceNotFound("Enrollment not found"));
        enrollmentRepository.delete(enrollment);
        course.getEnrollmentList().remove(enrollment);
        student.getEnrollmentList().remove(enrollment);
    }

    private boolean isCurrentUserInstructorOrStudentOfCourse(Long courseId) {
        // Verifica si es instructor del curso
        try {
            Instructor instructor = instructorService.getMe();
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFound("Course not found"));
            if (course.getInstructor() != null && course.getInstructor().getId().equals(instructor.getId())) {
                return true;
            }
        } catch (Exception ignored) {}

        // Verifica si es estudiante inscrito en el curso
        try {
            Student student = studentService.getMe();
            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
            return enrollments.stream().anyMatch(e -> e.getStudent().getId().equals(student.getId()));
        } catch (Exception ignored) {}

        return false;
    }

    public List<UserResponseDTO> getStudentsByCourse(Long courseId) {
        if (!isCurrentUserInstructorOrStudentOfCourse(courseId)) {
            throw new AccessDeniedException("You are not allowed to view the students of this course.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(Enrollment::getStudent)
                .map(student -> modelMapper.map(student, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public List<EnrollmentResponseDTO> getAllEnrollmentsAsDTO() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollments.stream().map(enrollment -> {
            EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
            dto.setId(enrollment.getId());
            dto.setEnrolled(enrollment.getEnrolled());
            
            // Course info
            EnrollmentResponseDTO.CourseInfoDTO courseInfo = new EnrollmentResponseDTO.CourseInfoDTO();
            courseInfo.setId(enrollment.getCourse().getId());
            courseInfo.setTitle(enrollment.getCourse().getTitle());
            courseInfo.setDescription(enrollment.getCourse().getDescription());
            courseInfo.setSection(enrollment.getCourse().getSection());
            courseInfo.setCategory(enrollment.getCourse().getCategory());
            dto.setCourse(courseInfo);
            
            // Student info
            EnrollmentResponseDTO.StudentInfoDTO studentInfo = new EnrollmentResponseDTO.StudentInfoDTO();
            studentInfo.setId(enrollment.getStudent().getId());
            studentInfo.setName(enrollment.getStudent().getName());
            studentInfo.setLastname(enrollment.getStudent().getLastname());
            studentInfo.setEmail(enrollment.getStudent().getEmail());
            dto.setStudent(studentInfo);
            
            return dto;
        }).collect(Collectors.toList());
    }

    public List<EnrollmentResponseDTO> getEnrollmentsByUserEmail(String email) {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return enrollments.stream()
                .filter(e -> e.getStudent().getEmail().equals(email))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateEnrollmentColor(Long enrollmentId, String color, String userEmail) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        // Validar que el usuario sea el dueño de la inscripción (student)
        if (!enrollment.getStudent().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permiso para modificar este curso");
        }
        enrollment.setColor(color);
        enrollmentRepository.save(enrollment);
    }

    private EnrollmentResponseDTO mapToDTO(Enrollment enrollment) {
        EnrollmentResponseDTO dto = new EnrollmentResponseDTO();
        dto.setId(enrollment.getId());
        dto.setEnrolled(enrollment.getEnrolled());
        dto.setColor(enrollment.getColor());

        // Mapear información del curso
        EnrollmentResponseDTO.CourseInfoDTO courseInfo = new EnrollmentResponseDTO.CourseInfoDTO();
        courseInfo.setId(enrollment.getCourse().getId());
        courseInfo.setTitle(enrollment.getCourse().getTitle());
        courseInfo.setDescription(enrollment.getCourse().getDescription());
        courseInfo.setSection(enrollment.getCourse().getSection());
        courseInfo.setCategory(enrollment.getCourse().getCategory());
        dto.setCourse(courseInfo);

        // Mapear información del estudiante
        EnrollmentResponseDTO.StudentInfoDTO studentInfo = new EnrollmentResponseDTO.StudentInfoDTO();
        studentInfo.setId(enrollment.getStudent().getId());
        studentInfo.setName(enrollment.getStudent().getName());
        studentInfo.setLastname(enrollment.getStudent().getLastname());
        studentInfo.setEmail(enrollment.getStudent().getEmail());
        dto.setStudent(studentInfo);

        return dto;
    }
}
