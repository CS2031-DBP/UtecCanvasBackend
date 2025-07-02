package com.example.gestiondecursos.Course.domain;

import com.example.gestiondecursos.Course.dto.CourseRequestDTO;
import com.example.gestiondecursos.Course.dto.CourseRequestForUpdateDTO;
import com.example.gestiondecursos.Course.dto.CourseResponseDTO;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    CourseRepository courseRepository;

    @Mock
    InstructorRepository instructorRepository;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CourseService courseService;

    @Test
    void getById_success() {
        Instructor instructor = new Instructor();
        instructor.setName("Juan");
        instructor.setLastname("Perez");

        Course course = new Course();
        course.setId(1L);
        course.setTitle("Matemáticas");
        course.setInstructor(instructor);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponseDTO dto = new CourseResponseDTO();
        when(modelMapper.map(course, CourseResponseDTO.class)).thenReturn(dto);

        dto.setInstructorName(instructor.getName());
        dto.setInstructorLastname(instructor.getLastname());

        CourseResponseDTO result = courseService.getById(1L);

        assertEquals(instructor.getName(), result.getInstructorName());
        assertEquals(instructor.getLastname(), result.getInstructorLastname());
    }

    @Test
    void createCourse_success() {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setTitle("Fisica");
        request.setDescription("Descripcion");
        request.setSection("A1");
        request.setCategory("Laboratorio");

        courseService.createCourse(request);

        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_success() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Old Title");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseRequestForUpdateDTO updateDTO = new CourseRequestForUpdateDTO();
        updateDTO.setTitle("New Title");

        courseService.updateCourse(1L, updateDTO);

        assertEquals("New Title", course.getTitle());
        verify(courseRepository).save(course);
    }

    @Test
    void getAllByTitle_success() {
        Course course = new Course();
        Instructor instructor = new Instructor();
        instructor.setName("Ana");
        instructor.setLastname("Lopez");
        course.setInstructor(instructor);
        course.setTitle("Programación");

        when(courseRepository.findAllByTitle("Programación")).thenReturn(List.of(course));
        when(modelMapper.map(any(Course.class), eq(CourseResponseDTO.class))).thenAnswer(invocation -> {
            Course c = invocation.getArgument(0);
            CourseResponseDTO dto = new CourseResponseDTO();
            dto.setInstructorName(c.getInstructor().getName());
            dto.setInstructorLastname(c.getInstructor().getLastname());
            dto.setTitle(c.getTitle());
            return dto;
        });

        List<CourseResponseDTO> result = courseService.getAllByTitle("Programación");

        assertEquals(1, result.size());
        assertEquals("Ana", result.get(0).getInstructorName());
    }

    @Test
    void assignInstructor_success() {
        Instructor instructor = new Instructor();
        instructor.setEmail("inst@uni.edu");
        instructor.setCourseList(new ArrayList<>());

        Course course = new Course();
        course.setId(1L);

        when(instructorRepository.findByEmail("inst@uni.edu")).thenReturn(Optional.of(instructor));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.assignInstructor("inst@uni.edu", 1L);

        assertTrue(instructor.getCourseList().contains(course));
        verify(courseRepository).save(course);
    }

    @Test
    void deleteCourse_success() {
        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        Mockito.doNothing().when(courseRepository).delete(course);

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }
}
