package com.example.gestiondecursos.Question.application;

//import com.example.gestiondecursos.Question.domain.Question;
//import com.example.gestiondecursos.Question.domain.QuestionService;
//import com.example.gestiondecursos.Question.dto.QuestionRequestDTO;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/question")
//@RequiredArgsConstructor
//public class QuestionController {
//    private final QuestionService questionService;
//
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    @PostMapping("/quizId/{id}")
//    public ResponseEntity<Void> createQuestion(@PathVariable Long id, @RequestBody Question question){
//        questionService.createQuestion(id, question);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    @PatchMapping("/questionId/{id}")
//    public ResponseEntity<Void> updateQuestion(@PathVariable Long id, @RequestBody @Valid QuestionRequestDTO question){
//        questionService.updateQuestion(id, question);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PreAuthorize("hasRole('INSTRUCTOR')")
//    @DeleteMapping("/questionId/{id}")
//    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id){
//        questionService.deleteQuestion(id);
//        return ResponseEntity.noContent().build();
//    }
//}
