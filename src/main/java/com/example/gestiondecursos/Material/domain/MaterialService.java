package com.example.gestiondecursos.Material.domain;

import com.example.gestiondecursos.Course.domain.CourseService;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Lesson.domain.Lesson;
import com.example.gestiondecursos.Lesson.infrastructure.LessonRepository;
import com.example.gestiondecursos.Material.dto.MaterialRequestDTO;
import com.example.gestiondecursos.Material.dto.MaterialResponseDTO;
import com.example.gestiondecursos.Material.infrastructure.MaterialRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final LessonRepository lessonRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseRepository courseRepository;

    public MaterialResponseDTO createMaterial(Long courseId, Integer week, MaterialRequestDTO material){
        validateUserAccessToCourse(courseId);

        Lesson lesson = lessonRepository.findByCourseIdAndWeek(courseId, week)
                .orElseThrow(() -> new ResourceNotFound("Lesson not found"));

        Material newMaterial = new Material();
        newMaterial.setTitle(material.getTitle());
        newMaterial.setLesson(lesson);
        newMaterial.setType(material.getType());
        newMaterial.setUrl(material.getUrl() != null ? material.getUrl() : "");

        Material savedMaterial = materialRepository.save(newMaterial);
        
        MaterialResponseDTO responseDTO = new MaterialResponseDTO();
        responseDTO.setId(savedMaterial.getId());
        responseDTO.setTitle(savedMaterial.getTitle());
        responseDTO.setType(savedMaterial.getType());
        responseDTO.setUrl(savedMaterial.getUrl());
        responseDTO.setCourseId(courseId);
        responseDTO.setLessonTitle(lesson.getTitle());
        
        return responseDTO;
    }

    private void validateUserAccessToCourse(Long courseId) {
        User user = userService.getAuthenticatedUser();

        if (user instanceof Student) {
            boolean enrolled = enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId);
            if (!enrolled) {
                throw new AccessDeniedException("Student not enrolled in this course");
            }
        }

        if (user instanceof Instructor) {
            boolean ownsCourse = courseRepository.existsByIdAndInstructorId(courseId, user.getId());
            if (!ownsCourse) {
                throw new AccessDeniedException("Instructor does not teach this course");
            }
        }
    }

    public List<MaterialResponseDTO> getMaterialsByCourse(Long courseId) {
        validateUserAccessToCourse(courseId);
        
        List<Material> materials = materialRepository.findByLessonCourseId(courseId);
        
        return materials.stream().map(material -> {
            MaterialResponseDTO dto = new MaterialResponseDTO();
            dto.setId(material.getId());
            dto.setTitle(material.getTitle());
            dto.setType(material.getType());
            dto.setUrl(material.getUrl());
            dto.setCourseId(courseId);
            dto.setLessonTitle(material.getLesson().getTitle());
            return dto;
        }).toList();
    }

    public List<MaterialResponseDTO> getMaterialsByLesson(Long lessonId) {
        List<Material> materials = materialRepository.findByLessonId(lessonId);
        
        return materials.stream().map(material -> {
            MaterialResponseDTO dto = new MaterialResponseDTO();
            dto.setId(material.getId());
            dto.setTitle(material.getTitle());
            dto.setType(material.getType());
            dto.setUrl(material.getUrl());
            dto.setCourseId(material.getLesson().getCourse().getId());
            dto.setLessonTitle(material.getLesson().getTitle());
            return dto;
        }).toList();
    }

    public void testLessonExists(Long courseId, Integer week) {
        lessonRepository.findByCourseIdAndWeek(courseId, week)
                .orElseThrow(() -> new ResourceNotFound("Lesson not found for course " + courseId + " and week " + week));
    }

    public MaterialResponseDTO updateMaterial(Long materialId, MaterialRequestDTO materialRequest) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFound("Material not found"));

        // Validate user access to the course
        validateUserAccessToCourse(material.getLesson().getCourse().getId());

        // Update material fields
        if (materialRequest.getTitle() != null) {
            material.setTitle(materialRequest.getTitle());
        }
        if (materialRequest.getType() != null) {
            material.setType(materialRequest.getType());
        }
        if (materialRequest.getUrl() != null) {
            material.setUrl(materialRequest.getUrl());
        }

        Material updatedMaterial = materialRepository.save(material);
        
        MaterialResponseDTO responseDTO = new MaterialResponseDTO();
        responseDTO.setId(updatedMaterial.getId());
        responseDTO.setTitle(updatedMaterial.getTitle());
        responseDTO.setType(updatedMaterial.getType());
        responseDTO.setUrl(updatedMaterial.getUrl());
        responseDTO.setCourseId(updatedMaterial.getLesson().getCourse().getId());
        responseDTO.setLessonTitle(updatedMaterial.getLesson().getTitle());
        
        return responseDTO;
    }

    public void deleteMaterial(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFound("Material not found"));

        // Validate user access to the course
        validateUserAccessToCourse(material.getLesson().getCourse().getId());

        materialRepository.delete(material);
    }
}