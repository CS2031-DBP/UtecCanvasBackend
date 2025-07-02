package com.example.gestiondecursos.Quiz.application;

import com.example.gestiondecursos.Quiz.Dto.QuizRequestDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizResponseDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizSubmissionDTO;
import com.example.gestiondecursos.Quiz.Dto.QuizSubmissionResponseDTO;
import com.example.gestiondecursos.Quiz.domain.Quiz;
import com.example.gestiondecursos.Quiz.domain.QuizService;
import com.example.gestiondecursos.Quiz.domain.QuizSubmission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/courseId/{courseId}")
    public ResponseEntity<Void> createQuiz(@PathVariable Long courseId,@RequestBody @Valid QuizRequestDTO quizRequest) {
        quizService.createQuiz(courseId, quizRequest);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PutMapping("/{quizId}")
    public ResponseEntity<Void> updateQuiz(@PathVariable Long quizId, @RequestBody @Valid QuizRequestDTO quizRequest) {
        quizService.updateQuiz(quizId, quizRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizResponseDTO>> getQuizzesByCourse(@PathVariable Long courseId) {
        List<QuizResponseDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        return ResponseEntity.ok(quizzes);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/title/{title}")
    public ResponseEntity<QuizResponseDTO> getQuizByTitle(@PathVariable String title) {
        QuizResponseDTO quizResponseDTO = quizService.getQuizByTitle(title);
        return ResponseEntity.ok(quizResponseDTO);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDTO> getQuizById(@PathVariable Long quizId) {
        QuizResponseDTO quizResponseDTO = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quizResponseDTO);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmissionResponseDTO> submitQuiz(@Valid @RequestBody QuizSubmissionDTO submissionDTO) {
        QuizSubmissionResponseDTO responseDTO = quizService.submitQuiz(submissionDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/delete/id/{quizId}")
    public ResponseEntity<Void> deleteQuizById(@PathVariable Long quizId) {
        quizService.deleteQuizById(quizId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @DeleteMapping("/delete/title/{title}")
    public ResponseEntity<Void> deleteQuizByTitle(@PathVariable String title) {
        quizService.deleteQuizByTitle(title);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/{quizId}/submissions")
    public ResponseEntity<List<QuizSubmissionResponseDTO>> getQuizSubmissions(@PathVariable Long quizId) {
        List<QuizSubmissionResponseDTO> submissions = quizService.getQuizSubmissionsByQuiz(quizId);
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{quizId}/submission/check")
    public ResponseEntity<Map<String, Object>> checkStudentSubmission(@PathVariable Long quizId) {
        boolean hasSubmitted = quizService.hasStudentSubmittedQuiz(quizId);
        return ResponseEntity.ok(Map.of("hasSubmitted", hasSubmitted));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/submission/{submissionId}/grade")
    public ResponseEntity<QuizSubmissionResponseDTO> gradeQuizSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Double> gradeRequest) {
        Double manualScore = gradeRequest.get("manualScore");
        Double finalScore = gradeRequest.get("finalScore");
        
        if (manualScore == null || finalScore == null) {
            return ResponseEntity.badRequest().build();
        }
        
        QuizSubmissionResponseDTO responseDTO = quizService.gradeQuizSubmission(submissionId, manualScore, finalScore);
        return ResponseEntity.ok(responseDTO);
    }

}

