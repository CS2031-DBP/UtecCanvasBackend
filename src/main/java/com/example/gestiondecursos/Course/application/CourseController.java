package com.example.gestiondecursos.Course.application;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.domain.CourseService;
import com.example.gestiondecursos.Course.dto.CourseRequestDTO;
import com.example.gestiondecursos.Course.dto.CourseRequestForUpdateDTO;
import com.example.gestiondecursos.Course.dto.CourseResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getById/{id}")
    public ResponseEntity<CourseResponseDTO> getById(@PathVariable Long id){
        CourseResponseDTO course = courseService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody @Valid CourseRequestDTO course){
        CourseResponseDTO createdCourse = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/courseId/{id}")
    public ResponseEntity<Void> updateCourse(@PathVariable Long id, @RequestBody @Valid CourseRequestForUpdateDTO course){
        courseService.updateCourse(id, course);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/getByTitle/{title}")
    public ResponseEntity<List<CourseResponseDTO>> getAllByTitle(@PathVariable String title){
        List<CourseResponseDTO> course = courseService.getAllByTitle(title);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/getByTitle/{title}/category/{category}")
    public ResponseEntity<CourseResponseDTO> getByTitleAndCategory(@PathVariable String title, @PathVariable String category){
        CourseResponseDTO course = courseService.getCourseByTitleAndCategory(title, category);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/getByTitle/{title}/section/{section}")
    public ResponseEntity<CourseResponseDTO> getByTitleAndSection(@PathVariable String title, @PathVariable String section){
        CourseResponseDTO course = courseService.getCourseByTitleAndSection(title, section);
        return ResponseEntity.status(HttpStatus.OK).body(course);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/courseId/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id){
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/instructor/{email}/courseId/{id}")
    public ResponseEntity<Void> assignInstructor(@PathVariable String email, @PathVariable Long id){
        courseService.assignInstructor(email, id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR')")
    @GetMapping("/myCourses")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesForCurrentUser() {
        List<CourseResponseDTO> courses = courseService.getCoursesForCurrentUser();
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/courseId/{id}/removeInstructor")
    public ResponseEntity<Void> removeInstructor(@PathVariable Long id){
        courseService.removeInstructor(id);
        return ResponseEntity.noContent().build();
    }
}
