package com.example.gestiondecursos.Assignment.domain;

import com.example.gestiondecursos.Assignment.Dto.AssignmentGradeDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentSubmissionDTO;
import com.example.gestiondecursos.Assignment.Dto.AssignmentSubmissionRequestDTO;
import com.example.gestiondecursos.Assignment.infrastructure.AssignmentRepository;
import com.example.gestiondecursos.Assignment.infrastructure.AssignmentSubmissionRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {
    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    public AssignmentSubmissionDTO submitAssignment(AssignmentSubmissionRequestDTO requestDTO) {
        User currentUser = userService.getAuthenticatedUser();
        
        if (!(currentUser instanceof Student)) {
            throw new AccessDeniedException("Only students can submit assignments");
        }

        Student student = (Student) currentUser;
        
        // Verificar que la tarea existe
        Assignment assignment = assignmentRepository.findById(requestDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFound("Assignment not found"));

        // Verificar que el estudiante está inscrito en el curso
        boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                student.getId(), assignment.getCourse().getId());
        if (!enrolled) {
            throw new AccessDeniedException("Student not enrolled in this course");
        }

        // Verificar si ya existe una submission
        submissionRepository.findByStudentIdAndAssignmentId(student.getId(), requestDTO.getAssignmentId())
                .ifPresent(existing -> {
                    throw new AccessDeniedException("Assignment already submitted");
                });

        // Crear la submission
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setSubmissionText(requestDTO.getSubmissionText());
        submission.setFileUrl(requestDTO.getFileUrl());
        submission.setStatus("SUBMITTED");
        submission.setSubmittedAt(LocalDateTime.now());

        AssignmentSubmission savedSubmission = submissionRepository.save(submission);
        
        return mapToDTO(savedSubmission);
    }

    public AssignmentSubmissionDTO gradeSubmission(Long submissionId, AssignmentGradeDTO gradeDTO) {
        User currentUser = userService.getAuthenticatedUser();
        
        if (!(currentUser instanceof com.example.gestiondecursos.Instructor.domain.Instructor)) {
            throw new AccessDeniedException("Only instructors can grade assignments");
        }

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFound("Submission not found"));

        // Verificar que el instructor es dueño del curso
        com.example.gestiondecursos.Instructor.domain.Instructor instructor = 
                (com.example.gestiondecursos.Instructor.domain.Instructor) currentUser;
        
        if (!submission.getAssignment().getCourse().getInstructor().getId().equals(instructor.getId())) {
            throw new AccessDeniedException("Instructor not authorized for this course");
        }

        // Actualizar la calificación
        submission.setScore(gradeDTO.getScore());
        submission.setFeedback(gradeDTO.getFeedback());
        submission.setStatus("GRADED");
        submission.setGradedAt(LocalDateTime.now());

        AssignmentSubmission savedSubmission = submissionRepository.save(submission);
        
        return mapToDTO(savedSubmission);
    }

    public List<AssignmentSubmissionDTO> getSubmissionsByAssignment(Long assignmentId) {
        User currentUser = userService.getAuthenticatedUser();
        
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFound("Assignment not found"));

        // Verificar permisos
        if (currentUser instanceof com.example.gestiondecursos.Instructor.domain.Instructor instructor) {
            if (!assignment.getCourse().getInstructor().getId().equals(instructor.getId())) {
                throw new AccessDeniedException("Instructor not authorized for this course");
            }
        } else if (currentUser instanceof Student student) {
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(
                    student.getId(), assignment.getCourse().getId());
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        }

        List<AssignmentSubmission> submissions = submissionRepository.findByAssignmentId(assignmentId);
        
        return submissions.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<AssignmentSubmissionDTO> getStudentSubmissions(Long studentId) {
        User currentUser = userService.getAuthenticatedUser();
        
        if (currentUser instanceof Student student) {
            if (!student.getId().equals(studentId)) {
                throw new AccessDeniedException("Students can only view their own submissions");
            }
        } else if (currentUser instanceof com.example.gestiondecursos.Instructor.domain.Instructor) {
            // Instructors can view any student's submissions
        } else {
            throw new AccessDeniedException("Access denied");
        }

        List<AssignmentSubmission> submissions = submissionRepository.findByStudentId(studentId);
        
        return submissions.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public boolean hasStudentSubmittedAssignment(Long assignmentId) {
        User currentUser = userService.getAuthenticatedUser();
        
        if (!(currentUser instanceof Student)) {
            throw new AccessDeniedException("Only students can check their own submissions");
        }

        Student student = (Student) currentUser;
        
        return submissionRepository.findByStudentIdAndAssignmentId(student.getId(), assignmentId)
                .isPresent();
    }

    private AssignmentSubmissionDTO mapToDTO(AssignmentSubmission submission) {
        AssignmentSubmissionDTO dto = modelMapper.map(submission, AssignmentSubmissionDTO.class);
        dto.setStudentId(submission.getStudent().getId());
        dto.setStudentName(submission.getStudent().getName());
        dto.setStudentLastname(submission.getStudent().getLastname());
        dto.setAssignmentId(submission.getAssignment().getId());
        dto.setAssignmentTitle(submission.getAssignment().getTitle());
        dto.setCourseId(submission.getAssignment().getCourse().getId());
        return dto;
    }
} 