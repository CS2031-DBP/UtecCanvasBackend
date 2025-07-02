package com.example.gestiondecursos;

import com.example.gestiondecursos.Auth.domain.AuthService;
import com.example.gestiondecursos.Config.JwtService;
import com.example.gestiondecursos.Course.domain.CourseService;
import com.example.gestiondecursos.Enrollment.domain.EnrollmentService;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Lesson.domain.LessonService;
import com.example.gestiondecursos.Material.application.MaterialControllerTest;
import com.example.gestiondecursos.Material.domain.MaterialService;
import com.example.gestiondecursos.Student.domain.StudentService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMockConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public StudentService studentService() {
        return Mockito.mock(StudentService.class);
    }


    @Bean
    public InstructorService instructorService() {
        return Mockito.mock(InstructorService.class);
    }

    @Bean
    public CourseService courseService(){
        return Mockito.mock(CourseService.class);
    }

    @Bean
    public EnrollmentService enrollmentService(){
        return Mockito.mock(EnrollmentService.class);
    }

    @Bean
    public MaterialService materialService(){
        return Mockito.mock(MaterialService.class);
    }

    @Bean
    public LessonService lessonService(){
        return Mockito.mock(LessonService.class);
    }
}