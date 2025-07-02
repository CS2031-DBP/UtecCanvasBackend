package com.example.gestiondecursos.Question.domain;

import com.example.gestiondecursos.Question.dto.QuestionRequestDTO;
import com.example.gestiondecursos.Question.infrastructure.QuestionRepository;
import com.example.gestiondecursos.Quiz.domain.Quiz;
import com.example.gestiondecursos.Quiz.infrastructure.QuizRepository;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//@Service
//@RequiredArgsConstructor
//public class QuestionService {
//    private final QuestionRepository questionRepository;
//    private final QuizRepository quizRepository;
//
//    public void createQuestion(Long quizId, Question question){
//        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new ResourceNotFound("Quiz not found"));
//        Question question1 = new Question();
//        question1.setQuestion(question.getQuestion());
//        question1.setQuiz(quiz);
//        questionRepository.save(question1);
//        quiz.getQuestions().add(question1);
//    }
//
//    public void updateQuestion(Long questionId, QuestionRequestDTO question){
//        Question question1 = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFound("Question not found"));
//        if(question.getQuestion() != null){
//            question1.setQuestion(question.getQuestion());
//        }
//        questionRepository.save(question1);
//    }
//
//    public void deleteQuestion(Long questionId){
//        Question question1 = questionRepository.findById(questionId).orElseThrow(() -> new ResourceNotFound("Question not found"));
//        questionRepository.delete(question1);
//    }
//}

