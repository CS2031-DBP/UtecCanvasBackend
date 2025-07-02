package com.example.gestiondecursos.Quiz.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Question.domain.Question;
import com.example.gestiondecursos.Question.domain.QuestionAnswer;
import com.example.gestiondecursos.Question.domain.QuestionType;
import com.example.gestiondecursos.Question.dto.QuestionAnswerDTO;
import com.example.gestiondecursos.Question.dto.QuestionRequestDTO;
import com.example.gestiondecursos.Question.dto.QuestionResponseDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizRequestDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizResponseDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizSubmissionDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizSubmissionResponseDTO;
import com.example.gestiondecursos.Quiz.infrastructure.QuizRepository;
import com.example.gestiondecursos.Quiz.infrastructure.QuizSubmissionRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository;

    public QuizResponseDTO createQuiz(Long courseId, QuizRequestDTO quizRequest) {
        User user = userService.getAuthenticatedUser();

        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can create quizzes");
        }

        boolean isInstructorOfCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
        if (!isInstructorOfCourse) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFound("Course not found"));

        // Verificar si ya existe un quiz con el mismo título en este curso
        Optional<Quiz> existingQuiz = quizRepository.findByTitleAndCourseId(quizRequest.getTitle(), courseId);
        if (existingQuiz.isPresent()) {
            throw new IllegalArgumentException("A quiz with the title '" + quizRequest.getTitle() + "' already exists in this course");
        }

        if (quizRequest.getQuestions() == null || quizRequest.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Quiz must have at least one question");
        }

        for (QuestionRequestDTO qdto : quizRequest.getQuestions()) {
            if (qdto.getQuestionType() == QuestionType.CLOSED) {
                if (qdto.getCorrectAnswer() == null || qdto.getCorrectAnswer().isBlank()) {
                    throw new IllegalArgumentException("True/False questions must have a correct answer");
                }
                if (!qdto.getCorrectAnswer().equals("Verdadero") && !qdto.getCorrectAnswer().equals("Falso")) {
                    throw new IllegalArgumentException("True/False questions must have 'Verdadero' or 'Falso' as correct answer");
                }
            } else if (qdto.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                if (qdto.getOptions() == null || qdto.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Multiple choice questions must have options");
                }
                if (qdto.getCorrectAnswer() == null || qdto.getCorrectAnswer().isBlank()) {
                    throw new IllegalArgumentException("Multiple choice questions must have a correct answer");
                }
                if (!qdto.getOptions().contains(qdto.getCorrectAnswer())) {
                    throw new IllegalArgumentException("Correct answer must be one of the options");
                }
            }
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setMaxScore(quizRequest.getMaxScore());
        quiz.setInstructions(quizRequest.getInstructions());
        quiz.setDueDate(quizRequest.getDueDate());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setCourse(course);

        List<Question> questionList = quizRequest.getQuestions().stream().map(qdto -> {
            Question q = new Question();
            q.setQuestion(qdto.getQuestion());
            q.setCorrectAnswer(qdto.getCorrectAnswer());
            q.setOptions(qdto.getOptions());
            q.setIsOpen(false);
            q.setQuestionType(qdto.getQuestionType());
            q.setQuiz(quiz);
            return q;
        }).toList();

        quiz.setQuestions(questionList);
        quizRepository.save(quiz);

        return modelMapper.map(quiz, QuizResponseDTO.class);
    }


    public QuizResponseDTO getQuizByTitle(String title) {
        User user = userService.getAuthenticatedUser();
        
        Quiz quiz = quizRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        // Verificar permisos según el rol
        if (user instanceof Student) {
            // Verificar que el estudiante está inscrito en el curso
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), quiz.getCourse().getId());
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        } else if (user instanceof Instructor) {
            // Verificar que el instructor es dueño del curso
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(quiz.getCourse().getId(), user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("You are not the instructor of this course");
            }
        } else {
            throw new AccessDeniedException("Access denied");
        }

        QuizResponseDTO dto = new QuizResponseDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setInstructions(quiz.getInstructions());
        dto.setDueDate(quiz.getDueDate());
        dto.setMaxScore(quiz.getMaxScore());
        dto.setCourseId(quiz.getCourse().getId());

        List<QuestionResponseDTO> questions = quiz.getQuestions().stream().map(q -> {
            QuestionResponseDTO qr = new QuestionResponseDTO();
            qr.setId(q.getId());
            qr.setQuestion(q.getQuestion());
            qr.setIsOpen(false);
            qr.setOptions(q.getOptions());
            return qr;
        }).toList();

        dto.setQuestions(questions);
        return dto;
    }

    public QuizResponseDTO getQuizById(Long quizId) {
        User user = userService.getAuthenticatedUser();
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        // Verificar permisos según el rol
        if (user instanceof Student) {
            // Verificar que el estudiante está inscrito en el curso
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), quiz.getCourse().getId());
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        } else if (user instanceof Instructor) {
            // Verificar que el instructor es dueño del curso
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(quiz.getCourse().getId(), user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("You are not the instructor of this course");
            }
        } else {
            throw new AccessDeniedException("Access denied");
        }

        QuizResponseDTO dto = new QuizResponseDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setInstructions(quiz.getInstructions());
        dto.setDueDate(quiz.getDueDate());
        dto.setMaxScore(quiz.getMaxScore());
        dto.setCourseId(quiz.getCourse().getId());

        List<QuestionResponseDTO> questions = quiz.getQuestions().stream().map(q -> {
            QuestionResponseDTO qr = new QuestionResponseDTO();
            qr.setId(q.getId());
            qr.setQuestion(q.getQuestion());
            qr.setIsOpen(false);
            qr.setOptions(q.getOptions());
            return qr;
        }).toList();

        dto.setQuestions(questions);
        return dto;
    }

    @Transactional
    public QuizSubmissionResponseDTO submitQuiz(QuizSubmissionDTO submissionDTO) {
        User user = userService.getAuthenticatedUser();
        if (!(user instanceof Student student)) {
            throw new AccessDeniedException("Only students can submit quizzes.");
        }

        Quiz quiz = quizRepository.findById(submissionDTO.getQuizId())
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        // Verificar si el estudiante ya ha hecho este quiz
        boolean alreadySubmitted = quizSubmissionRepository.existsByStudentIdAndQuizId(student.getId(), quiz.getId());
        if (alreadySubmitted) {
            throw new AccessDeniedException("You have already submitted this quiz.");
        }

        // Verificar si la fecha límite ha pasado
        if (quiz.getDueDate() != null && LocalDateTime.now().isAfter(quiz.getDueDate())) {
            throw new AccessDeniedException("The quiz submission deadline has passed.");
        }

        QuizSubmission submission = new QuizSubmission();
        submission.setStudent(student);
        submission.setQuiz(quiz);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus("SUBMITTED");

        Map<Long, String> answersMap = submissionDTO.getAnswers().stream()
                .collect(Collectors.toMap(QuestionAnswerDTO::getQuestionId, QuestionAnswerDTO::getAnswer));

        double totalCorrect = 0.0;
        int totalQuestions = quiz.getQuestions().size();

        for (Question question : quiz.getQuestions()) {
            String answer = answersMap.get(question.getId());

            QuestionAnswer qa = new QuestionAnswer();
            qa.setQuestion(question);
            qa.setAnswer(answer);
            qa.setSubmission(submission);
            submission.getAnswers().add(qa);

            // All questions are auto-gradable (multiple choice or true/false)
            if (question.getCorrectAnswer() != null && question.getCorrectAnswer().equalsIgnoreCase(answer)) {
                totalCorrect += 1.0;
            }
        }

        // Calculate automatic score for all questions
        double automaticScore = (totalCorrect / totalQuestions) * quiz.getMaxScore();
        submission.setAutomaticScore(automaticScore);
        submission.setFinalScore(automaticScore);
        submission.setStatus("GRADED");
        submission.setGradedAt(LocalDateTime.now());

        QuizSubmission saved = quizSubmissionRepository.save(submission);

        QuizSubmissionResponseDTO responseDTO = new QuizSubmissionResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setQuizId(saved.getQuiz().getId());
        responseDTO.setStudentId(saved.getStudent().getId());
        responseDTO.setAutomaticScore(saved.getAutomaticScore());
        responseDTO.setManualScore(saved.getManualScore());
        responseDTO.setFinalScore(saved.getFinalScore());
        responseDTO.setStatus(saved.getStatus());
        responseDTO.setSubmittedAt(saved.getSubmittedAt());
        responseDTO.setGradedAt(saved.getGradedAt());

        return responseDTO;
    }

    @Transactional
    public QuizSubmissionResponseDTO gradeQuizSubmission(Long submissionId, Double manualScore, Double finalScore) {
        User user = userService.getAuthenticatedUser();
        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can grade quiz submissions.");
        }

        QuizSubmission submission = quizSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFound("Quiz submission not found"));

        // Verify instructor owns the course
        if (!submission.getQuiz().getCourse().getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        submission.setManualScore(manualScore);
        submission.setFinalScore(finalScore);
        submission.setStatus("GRADED");
        submission.setGradedAt(LocalDateTime.now());

        QuizSubmission saved = quizSubmissionRepository.save(submission);

        QuizSubmissionResponseDTO responseDTO = new QuizSubmissionResponseDTO();
        responseDTO.setId(saved.getId());
        responseDTO.setQuizId(saved.getQuiz().getId());
        responseDTO.setStudentId(saved.getStudent().getId());
        responseDTO.setAutomaticScore(saved.getAutomaticScore());
        responseDTO.setManualScore(saved.getManualScore());
        responseDTO.setFinalScore(saved.getFinalScore());
        responseDTO.setStatus(saved.getStatus());
        responseDTO.setSubmittedAt(saved.getSubmittedAt());
        responseDTO.setGradedAt(saved.getGradedAt());

        return responseDTO;
    }

    public List<QuizSubmissionResponseDTO> getQuizSubmissionsByQuiz(Long quizId) {
        User user = userService.getAuthenticatedUser();
        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can view quiz submissions.");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        // Verify instructor owns the course
        if (!quiz.getCourse().getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuizIdOrderBySubmittedAtDesc(quizId);
        
        return submissions.stream().map(submission -> {
            QuizSubmissionResponseDTO dto = new QuizSubmissionResponseDTO();
            dto.setId(submission.getId());
            dto.setQuizId(submission.getQuiz().getId());
            dto.setStudentId(submission.getStudent().getId());
            dto.setAutomaticScore(submission.getAutomaticScore());
            dto.setManualScore(submission.getManualScore());
            dto.setFinalScore(submission.getFinalScore());
            dto.setStatus(submission.getStatus());
            dto.setSubmittedAt(submission.getSubmittedAt());
            dto.setGradedAt(submission.getGradedAt());
            return dto;
        }).toList();
    }

    @Transactional
    public void deleteQuizById(Long quizId) {
        User user = userService.getAuthenticatedUser();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can delete quizzes");
        }

        if (!quiz.getCourse().getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        quizRepository.deleteById(quizId);
    }

    @Transactional
    public void deleteQuizByTitle(String title) {
        User user = userService.getAuthenticatedUser();

        Quiz quiz = quizRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can delete quizzes");
        }

        if (!quiz.getCourse().getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        quizRepository.delete(quiz);
    }

    public List<QuizResponseDTO> getQuizzesByCourse(Long courseId) {
        User user = userService.getAuthenticatedUser();
        
        if (user instanceof Student) {
            // For students, we could add enrollment validation here if needed
        } else if (user instanceof Instructor) {
            boolean isInstructorOfCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!isInstructorOfCourse) {
                throw new AccessDeniedException("You are not the instructor of this course");
            }
        }

        List<Quiz> quizzes = quizRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        
        return quizzes.stream()
                .map(quiz -> {
                    QuizResponseDTO dto = new QuizResponseDTO();
                    dto.setId(quiz.getId());
                    dto.setTitle(quiz.getTitle());
                    dto.setInstructions(quiz.getInstructions());
                    dto.setDueDate(quiz.getDueDate());
                    dto.setMaxScore(quiz.getMaxScore());
                    dto.setCourseId(quiz.getCourse().getId());
                    
                    List<QuestionResponseDTO> questions = quiz.getQuestions().stream().map(q -> {
                        QuestionResponseDTO qr = new QuestionResponseDTO();
                        qr.setId(q.getId());
                        qr.setQuestion(q.getQuestion());
                        qr.setIsOpen(false);
                        qr.setOptions(q.getOptions());
                        return qr;
                    }).toList();
                    
                    dto.setQuestions(questions);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public void updateQuiz(Long quizId, QuizRequestDTO quizRequest) {
        User user = userService.getAuthenticatedUser();

        if (!(user instanceof Instructor)) {
            throw new AccessDeniedException("Only instructors can update quizzes");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFound("Quiz not found"));

        // Verify instructor owns the course
        if (!quiz.getCourse().getInstructor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the instructor of this course");
        }

        // Verificar si ya existe otro quiz con el mismo título en este curso (excluyendo el actual)
        Optional<Quiz> existingQuiz = quizRepository.findByTitleAndCourseId(quizRequest.getTitle(), quiz.getCourse().getId());
        if (existingQuiz.isPresent() && !existingQuiz.get().getId().equals(quizId)) {
            throw new IllegalArgumentException("A quiz with the title '" + quizRequest.getTitle() + "' already exists in this course");
        }

        if (quizRequest.getQuestions() == null || quizRequest.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Quiz must have at least one question");
        }

        // Validate questions
        for (QuestionRequestDTO qdto : quizRequest.getQuestions()) {
            if (qdto.getQuestionType() == QuestionType.CLOSED) {
                if (qdto.getCorrectAnswer() == null || qdto.getCorrectAnswer().isBlank()) {
                    throw new IllegalArgumentException("True/False questions must have a correct answer");
                }
                if (!qdto.getCorrectAnswer().equals("Verdadero") && !qdto.getCorrectAnswer().equals("Falso")) {
                    throw new IllegalArgumentException("True/False questions must have 'Verdadero' or 'Falso' as correct answer");
                }
            } else if (qdto.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                if (qdto.getOptions() == null || qdto.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Multiple choice questions must have options");
                }
                if (qdto.getCorrectAnswer() == null || qdto.getCorrectAnswer().isBlank()) {
                    throw new IllegalArgumentException("Multiple choice questions must have a correct answer");
                }
                if (!qdto.getOptions().contains(qdto.getCorrectAnswer())) {
                    throw new IllegalArgumentException("Correct answer must be one of the options");
                }
            }
        }

        // Update quiz basic info
        quiz.setTitle(quizRequest.getTitle());
        quiz.setMaxScore(quizRequest.getMaxScore());
        quiz.setInstructions(quizRequest.getInstructions());
        quiz.setDueDate(quizRequest.getDueDate());

        // Create new questions list
        List<Question> newQuestions = quizRequest.getQuestions().stream().map(qdto -> {
            Question q = new Question();
            q.setQuestion(qdto.getQuestion());
            q.setCorrectAnswer(qdto.getCorrectAnswer());
            q.setOptions(qdto.getOptions());
            q.setIsOpen(false);
            q.setQuestionType(qdto.getQuestionType());
            q.setQuiz(quiz);
            return q;
        }).toList();

        // Set the new questions list (this replaces the old one)
        quiz.setQuestions(newQuestions);
        
        // Save the updated quiz
        quizRepository.save(quiz);
    }

    public boolean hasStudentSubmittedQuiz(Long quizId) {
        User user = userService.getAuthenticatedUser();
        if (!(user instanceof Student student)) {
            throw new AccessDeniedException("Only students can check quiz submissions.");
        }

        return quizSubmissionRepository.existsByStudentIdAndQuizId(student.getId(), quizId);
    }
}
