package com.example.gestiondecursos.Evaluation.domain;

import com.example.gestiondecursos.Assignment.Dto.AssignmentSubmissionDTO;
import com.example.gestiondecursos.Assignment.domain.AssignmentSubmissionService;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Quiz.Dto.QuizSubmissionResponseDTO;
import com.example.gestiondecursos.Quiz.domain.QuizService;
import com.example.gestiondecursos.Quiz.infrastructure.QuizRepository;
import com.example.gestiondecursos.Quiz.infrastructure.QuizSubmissionRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.User.dto.UserResponseDTO;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final AssignmentSubmissionService assignmentSubmissionService;
    private final QuizService quizService;
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;

    public List<GradeDTO> getStudentGrades(Long studentId, Long courseId) {
        User currentUser = userService.getAuthenticatedUser();
        
        // Verificar permisos
        if (currentUser instanceof Student student) {
            if (!student.getId().equals(studentId)) {
                throw new AccessDeniedException("Students can only view their own grades");
            }
        } else if (currentUser instanceof com.example.gestiondecursos.Instructor.domain.Instructor) {
            // Instructors can view any student's grades
        } else {
            throw new AccessDeniedException("Access denied");
        }

        // Verificar que el estudiante está inscrito en el curso
        boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
        if (!enrolled) {
            throw new AccessDeniedException("Student not enrolled in this course");
        }

        List<GradeDTO> grades = new ArrayList<>();

        // Obtener calificaciones de assignments del curso específico
        List<AssignmentSubmissionDTO> assignmentSubmissions = assignmentSubmissionService.getStudentSubmissions(studentId);
        for (AssignmentSubmissionDTO submission : assignmentSubmissions) {
            // Solo incluir assignments del curso específico
            if (submission.getCourseId() != null && submission.getCourseId().equals(courseId)) {
                grades.add(new GradeDTO(
                    submission.getAssignmentTitle(),
                    "ASSIGNMENT",
                    submission.getScore(),
                    submission.getStatus().equals("GRADED"),
                    submission.getSubmittedAt().toString()
                ));
            }
        }

        // Obtener calificaciones de quizzes del curso específico
        List<com.example.gestiondecursos.Quiz.domain.Quiz> courseQuizzes = quizRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        for (com.example.gestiondecursos.Quiz.domain.Quiz quiz : courseQuizzes) {
            // Verificar si el estudiante ha hecho este quiz
            Optional<com.example.gestiondecursos.Quiz.domain.QuizSubmission> submissionOpt = 
                quizSubmissionRepository.findByStudentIdAndQuizId(studentId, quiz.getId());
            
            if (submissionOpt.isPresent()) {
                com.example.gestiondecursos.Quiz.domain.QuizSubmission submission = submissionOpt.get();
                Double finalScore = submission.getFinalScore() != null ? 
                    submission.getFinalScore() : submission.getAutomaticScore();
                
                grades.add(new GradeDTO(
                    quiz.getTitle(),
                    "QUIZ",
                    finalScore,
                    submission.getStatus().equals("GRADED"),
                    submission.getSubmittedAt().toString()
                ));
            }
        }
        
        return grades;
    }

    public List<StudentGradesDTO> getAllStudentsGrades(Long courseId) {
        User currentUser = userService.getAuthenticatedUser();
        
        // Verificar que es instructor
        if (!(currentUser instanceof com.example.gestiondecursos.Instructor.domain.Instructor)) {
            throw new AccessDeniedException("Only instructors can view all students' grades");
        }

        // Obtener todos los estudiantes del curso
        List<UserResponseDTO> students = enrollmentRepository.findByCourseId(courseId).stream()
                .map(enrollment -> modelMapper.map(enrollment.getStudent(), UserResponseDTO.class))
                .collect(Collectors.toList());

        List<StudentGradesDTO> allStudentsGrades = new ArrayList<>();

        for (UserResponseDTO student : students) {
            List<GradeDTO> studentGrades = getStudentGrades(student.getId(), courseId);
            
            // Calcular promedio del estudiante
            double average = 0.0;
            int gradedCount = 0;
            for (GradeDTO grade : studentGrades) {
                if (grade.getGraded() && grade.getScore() != null) {
                    average += grade.getScore();
                    gradedCount++;
                }
            }
            
            if (gradedCount > 0) {
                average = average / gradedCount;
            }

            allStudentsGrades.add(new StudentGradesDTO(
                student,
                studentGrades,
                average,
                gradedCount
            ));
        }

        return allStudentsGrades;
    }

    public static class GradeDTO {
        private String evaluationName;
        private String evaluationType;
        private Double score;
        private boolean graded;
        private String submittedAt;

        public GradeDTO(String evaluationName, String evaluationType, Double score, boolean graded, String submittedAt) {
            this.evaluationName = evaluationName;
            this.evaluationType = evaluationType;
            this.score = score;
            this.graded = graded;
            this.submittedAt = submittedAt;
        }

        // Getters and setters
        public String getEvaluationName() { return evaluationName; }
        public void setEvaluationName(String evaluationName) { this.evaluationName = evaluationName; }
        
        public String getEvaluationType() { return evaluationType; }
        public void setEvaluationType(String evaluationType) { this.evaluationType = evaluationType; }
        
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        
        public boolean getGraded() { return graded; }
        public void setGraded(boolean graded) { this.graded = graded; }
        
        public String getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
    }

    public static class StudentGradesDTO {
        private UserResponseDTO student;
        private List<GradeDTO> grades;
        private double average;
        private int gradedCount;

        public StudentGradesDTO(UserResponseDTO student, List<GradeDTO> grades, double average, int gradedCount) {
            this.student = student;
            this.grades = grades;
            this.average = average;
            this.gradedCount = gradedCount;
        }

        // Getters and setters
        public UserResponseDTO getStudent() { return student; }
        public void setStudent(UserResponseDTO student) { this.student = student; }
        
        public List<GradeDTO> getGrades() { return grades; }
        public void setGrades(List<GradeDTO> grades) { this.grades = grades; }
        
        public double getAverage() { return average; }
        public void setAverage(double average) { this.average = average; }
        
        public int getGradedCount() { return gradedCount; }
        public void setGradedCount(int gradedCount) { this.gradedCount = gradedCount; }
    }
} 