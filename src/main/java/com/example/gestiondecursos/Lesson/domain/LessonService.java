package com.example.gestiondecursos.Lesson.domain;


import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Lesson.dto.LessonRequestDTO;
import com.example.gestiondecursos.Lesson.dto.LessonResponseDTO;
import com.example.gestiondecursos.Lesson.infrastructure.LessonRepository;
import com.example.gestiondecursos.Material.domain.Material;
import com.example.gestiondecursos.Material.domain.MaterialService;
import com.example.gestiondecursos.Material.dto.MaterialResponseDTO;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final MaterialService materialService;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final StudentRepository studentRepository;

    public void createLesson(Long courseId, LessonRequestDTO lessonRequest) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFound("Course not found"));
        Lesson lesson = new Lesson();
        lesson.setWeek(lessonRequest.getWeek());
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setCourse(course);
        lessonRepository.save(lesson);
    }


    public LessonResponseDTO getLessonByTitle(Long courseId,String title){
        User user = userService.getAuthenticatedUser();
        if(user instanceof Student){
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if(!enrolled){
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        }
        if(user instanceof Instructor){
            boolean enrolled = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if(!enrolled){
                throw new AccessDeniedException("Instructor not enrolled in this course");
            }
        }
        Lesson lesson = lessonRepository.findByCourseIdAndTitle(courseId, title)
                .orElseThrow(() -> new ResourceNotFound("Lesson not found"));

        LessonResponseDTO dto = modelMapper.map(lesson, LessonResponseDTO.class);
        dto.setCourseTitle(lesson.getCourse().getTitle());
        dto.setMaterialTitles(lesson.getMaterials().stream().map(Material::getTitle).toList());
        
        // Add complete materials
        List<MaterialResponseDTO> materials = lesson.getMaterials().stream()
                .map(material -> {
                    MaterialResponseDTO materialDto = new MaterialResponseDTO();
                    materialDto.setId(material.getId());
                    materialDto.setTitle(material.getTitle());
                    materialDto.setType(material.getType());
                    materialDto.setUrl(material.getUrl());
                    materialDto.setCourseId(courseId);
                    return materialDto;
                })
                .toList();
        dto.setMaterials(materials);
        
        return dto;
    }

    public LessonResponseDTO getLessonByWeek(Long courseId, Integer week){
        User user = userService.getAuthenticatedUser();
        if(user instanceof Student){
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if(!enrolled){
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        }
        if(user instanceof Instructor){
            boolean enrolled = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if(!enrolled){
                throw new AccessDeniedException("Instructor not enrolled in this course");
            }
        }
        Lesson lesson = lessonRepository.findByCourseIdAndWeek(courseId, week)
                .orElseThrow(() -> new ResourceNotFound("Lesson not found"));

        LessonResponseDTO dto = modelMapper.map(lesson, LessonResponseDTO.class);
        dto.setCourseTitle(lesson.getCourse().getTitle());
        dto.setMaterialTitles(lesson.getMaterials().stream().map(Material::getTitle).toList());
        
        // Add complete materials
        List<MaterialResponseDTO> materials = lesson.getMaterials().stream()
                .map(material -> {
                    MaterialResponseDTO materialDto = new MaterialResponseDTO();
                    materialDto.setId(material.getId());
                    materialDto.setTitle(material.getTitle());
                    materialDto.setType(material.getType());
                    materialDto.setUrl(material.getUrl());
                    materialDto.setCourseId(courseId);
                    return materialDto;
                })
                .toList();
        dto.setMaterials(materials);
        
        return dto;
    }

    public void deleteLesson(Long id){
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Lesson not found"));
        lessonRepository.delete(lesson);
    }

    public void updateLesson(Long id, LessonRequestDTO lesson){
        Lesson lesson1 = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Lesson not found"));
        if(lesson.getTitle() != null) {
            lesson1.setTitle(lesson.getTitle());
        }
        lessonRepository.save(lesson1);
    }

    public List<LessonResponseDTO> getLessonsByCourse(Long courseId) {
        User user = userService.getAuthenticatedUser();
        if(user instanceof Student){
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if(!enrolled){
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        }
        if(user instanceof Instructor){
            boolean enrolled = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if(!enrolled){
                throw new AccessDeniedException("Instructor not enrolled in this course");
            }
        }

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByWeekAsc(courseId);
        
        return lessons.stream()
                .map(lesson -> {
                    LessonResponseDTO dto = modelMapper.map(lesson, LessonResponseDTO.class);
                    dto.setCourseTitle(lesson.getCourse().getTitle());
                    dto.setMaterialTitles(lesson.getMaterials().stream().map(Material::getTitle).toList());
                    
                    // Add complete materials
                    List<MaterialResponseDTO> materials = lesson.getMaterials().stream()
                            .map(material -> {
                                MaterialResponseDTO materialDto = new MaterialResponseDTO();
                                materialDto.setId(material.getId());
                                materialDto.setTitle(material.getTitle());
                                materialDto.setType(material.getType());
                                materialDto.setUrl(material.getUrl());
                                materialDto.setCourseId(courseId);
                                return materialDto;
                            })
                            .toList();
                    dto.setMaterials(materials);
                    
                    return dto;
                })
                .toList();
    }
}
