package com.example.gestiondecursos.Quiz.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Quiz.Dto.QuizRequestDTO;
import com.example.gestiondecursos.Quiz.infrastructure.QuizRepository;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private QuizService quizService;

    private Instructor instructor;
    private Course course1;
    private Course course2;
    private QuizRequestDTO quizRequest;

    @BeforeEach
    void setUp() {
        instructor = new Instructor();
        instructor.setId(1L);

        course1 = new Course();
        course1.setId(1L);
        course1.setInstructor(instructor);

        course2 = new Course();
        course2.setId(2L);
        course2.setInstructor(instructor);

        quizRequest = new QuizRequestDTO();
        quizRequest.setTitle("Quiz 1");
        quizRequest.setMaxScore(100.0);
        quizRequest.setQuestions(Arrays.asList());
    }

    @Test
    void shouldAllowCreatingQuizWithSameTitleInDifferentCourses() {
        // Given
        when(userService.getAuthenticatedUser()).thenReturn(instructor);
        when(quizRepository.findByTitleAndCourseId("Quiz 1", 1L)).thenReturn(Optional.empty());
        when(quizRepository.findByTitleAndCourseId("Quiz 1", 2L)).thenReturn(Optional.empty());
        when(quizRepository.save(any(Quiz.class))).thenReturn(new Quiz());

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> {
            quizService.createQuiz(1L, quizRequest);
            quizService.createQuiz(2L, quizRequest);
        });
    }

    @Test
    void shouldPreventCreatingQuizWithSameTitleInSameCourse() {
        // Given
        Quiz existingQuiz = new Quiz();
        existingQuiz.setTitle("Quiz 1");
        existingQuiz.setCourse(course1);

        when(userService.getAuthenticatedUser()).thenReturn(instructor);
        when(quizRepository.findByTitleAndCourseId("Quiz 1", 1L)).thenReturn(Optional.of(existingQuiz));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            quizService.createQuiz(1L, quizRequest);
        });

        assertTrue(exception.getMessage().contains("already exists in this course"));
    }
} 