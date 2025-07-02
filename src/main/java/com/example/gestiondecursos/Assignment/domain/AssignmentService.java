package com.example.gestiondecursos.Assignment.domain;

import com.example.gestiondecursos.Assignment.Dto.AssignmentRequestDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentResponseDTO;
import com.example.gestiondecursos.Assignment.infrastructure.AssignmentRepository;
import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentResponseDTO createAssignment(Long courseId, AssignmentRequestDTO assignment){
        User user = userService.getAuthenticatedUser();

        if (user instanceof Instructor) {
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("Instructor not authorized for this course");
            }
        }
        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can create assignments");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFound("Course not found"));

        Assignment newAssignment = new Assignment();
        newAssignment.setCourse(course);
        newAssignment.setTitle(assignment.getTitle());
        newAssignment.setInstructions(assignment.getDescription());
        newAssignment.setMaxScore(assignment.getMaxScore() != null ? assignment.getMaxScore().doubleValue() : 100.0);
        newAssignment.setCreatedAt(LocalDateTime.now());
        newAssignment.setDueDate(assignment.getDueDate());
        newAssignment.setMaterial(assignment.getMaterial());
        newAssignment.setUploadRequired(assignment.getUploadRequired() != null ? assignment.getUploadRequired() : false);

        Assignment saved = assignmentRepository.save(newAssignment);
        course.getEvaluations().add(saved);

        AssignmentResponseDTO responseDTO = new AssignmentResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setTitle(saved.getTitle());
        responseDTO.setDescription(saved.getInstructions());
        responseDTO.setMaxScore(saved.getMaxScore() != null ? saved.getMaxScore().intValue() : 100);
        responseDTO.setCreatedAt(saved.getCreatedAt());
        responseDTO.setDueDate(saved.getDueDate());
        responseDTO.setMaterial(saved.getMaterial());
        responseDTO.setUploadRequired(saved.getUploadRequired());
        responseDTO.setCourseId(courseId);
        
        return responseDTO;
    }


    public void updateAssignment(Long id, AssignmentRequestDTO assignment){
        Assignment assignment1 = assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Assignment not found"));
        validateInstructorAccessToAssignment(assignment1);
        if(assignment.getTitle() != null){
            assignment1.setTitle(assignment.getTitle());
        }
        if(assignment.getMaxScore() != null) {
            assignment1.setMaxScore(assignment.getMaxScore().doubleValue());
        }
        if(assignment.getDescription() != null){
            assignment1.setInstructions(assignment.getDescription());
        }
        if(assignment.getDueDate() != null){
            assignment1.setDueDate(assignment.getDueDate());
        }
        if(assignment.getMaterial() != null){
            assignment1.setMaterial(assignment.getMaterial());
        }
        if(assignment.getUploadRequired() != null){
            assignment1.setUploadRequired(assignment.getUploadRequired());
        }
        assignmentRepository.save(assignment1);
    }

    public void deleteAssignment(Long id){
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Assignment not found"));
        validateInstructorAccessToAssignment(assignment);
        assignmentRepository.delete(assignment);
    }

    public AssignmentResponseDTO getByTitle(String title){
        Assignment assignment = assignmentRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFound("Assignment not found"));

        User user = userService.getAuthenticatedUser();
        Long courseId = assignment.getCourse().getId();

        if (user instanceof Student) {
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        } else if (user instanceof Instructor) {
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("Instructor not authorized");
            }
        }

        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getInstructions());
        dto.setMaxScore(assignment.getMaxScore() != null ? assignment.getMaxScore().intValue() : 100);
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setDueDate(assignment.getDueDate());
        dto.setMaterial(assignment.getMaterial());
        dto.setUploadRequired(assignment.getUploadRequired());
        dto.setCourseId(courseId);
        
        return dto;
    }

    public AssignmentResponseDTO getAssignmentById(Long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Assignment not found"));

        User user = userService.getAuthenticatedUser();
        Long courseId = assignment.getCourse().getId();

        if (user instanceof Student) {
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        } else if (user instanceof Instructor) {
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("Instructor not authorized");
            }
        }

        AssignmentResponseDTO dto = new AssignmentResponseDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getInstructions());
        dto.setMaxScore(assignment.getMaxScore() != null ? assignment.getMaxScore().intValue() : 100);
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setDueDate(assignment.getDueDate());
        dto.setMaterial(assignment.getMaterial());
        dto.setUploadRequired(assignment.getUploadRequired());
        dto.setCourseId(courseId);
        
        return dto;
    }

    private void validateInstructorAccessToAssignment(Assignment assignment) {
        User user = userService.getAuthenticatedUser();
        if (user instanceof Instructor) {
            if (!assignment.getCourse().getInstructor().getId().equals(user.getId())) {
                throw new AccessDeniedException("Instructor not authorized to modify this assignment");
            }
        } else {
            throw new AccessDeniedException("Only instructors can modify assignments");
        }
    }

    public List<AssignmentResponseDTO> getAssignmentsByCourse(Long courseId) {
        User user = userService.getAuthenticatedUser();
        
        // Verificar que el curso existe
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFound("Course not found"));

        // Verificar permisos según el rol
        if (user instanceof Student) {
            // Verificar que el estudiante está inscrito en el curso
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        } else if (user instanceof Instructor) {
            // Verificar que el instructor es dueño del curso
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("Instructor not authorized for this course");
            }
        } else {
            throw new AccessDeniedException("Access denied");
        }

        // Obtener todas las tareas del curso
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        
        // Convertir a DTOs
        return assignments.stream()
                .map(assignment -> {
                    AssignmentResponseDTO dto = new AssignmentResponseDTO();
                    dto.setId(assignment.getId());
                    dto.setTitle(assignment.getTitle());
                    dto.setDescription(assignment.getInstructions());
                    dto.setMaxScore(assignment.getMaxScore() != null ? assignment.getMaxScore().intValue() : 100);
                    dto.setCreatedAt(assignment.getCreatedAt());
                    dto.setDueDate(assignment.getDueDate());
                    dto.setMaterial(assignment.getMaterial());
                    dto.setUploadRequired(assignment.getUploadRequired());
                    dto.setCourseId(courseId);
                    return dto;
                })
                .toList();
    }

//    public List<Assignment> getAllAssignmentsForCourse(Long courseId){
//
//    }

}
