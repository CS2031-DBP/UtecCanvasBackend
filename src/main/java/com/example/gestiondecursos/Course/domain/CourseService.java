package com.example.gestiondecursos.Course.domain;

import com.example.gestiondecursos.Course.dto.CourseRequestDTO;
import com.example.gestiondecursos.Course.dto.CourseRequestForUpdateDTO;
import com.example.gestiondecursos.Course.dto.CourseResponseDTO;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.Enrollment.domain.Enrollment;
import com.example.gestiondecursos.Enrollment.infrastructure.EnrollmentRepository;
import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.domain.StudentService;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final StudentService studentService;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorService instructorService;

    private void setCourseStatistics(Course course, CourseResponseDTO dto) {
        // Calculate statistics
        dto.setLessonCount(course.getLessonList().size());
        
        // Count assignments and quizzes from evaluations
        long assignmentCount = course.getEvaluations().stream()
                .filter(evaluation -> evaluation instanceof com.example.gestiondecursos.Assignment.domain.Assignment)
                .count();
        long quizCount = course.getEvaluations().stream()
                .filter(evaluation -> evaluation instanceof com.example.gestiondecursos.Quiz.domain.Quiz)
                .count();
        
        dto.setAssignmentCount((int) assignmentCount);
        dto.setQuizCount((int) quizCount);
    }

    public CourseResponseDTO getById(Long id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Course Not Found"));
        CourseResponseDTO courseResponseDTO = modelMapper.map(course, CourseResponseDTO.class);

        Instructor instructor = course.getInstructor();
        if(instructor == null){
            throw new ResourceIsNullException("There is not Instructor assigned to this course");
        }
        if(instructor.getName() == null){
            throw new ResourceIsNullException("There is not Instructor name to link");
        }
        if(instructor.getLastname() == null){
            throw new ResourceIsNullException("There is not Instructor lastname to link");
        }
        courseResponseDTO.setInstructorName(instructor.getName());
        courseResponseDTO.setInstructorLastname(instructor.getLastname());
        courseResponseDTO.setInstructorEmail(instructor.getEmail());
        courseResponseDTO.setInstructorProfilePhoto(instructor.getProfilePhoto());
        
        setCourseStatistics(course, courseResponseDTO);
        
        return courseResponseDTO;
    }

    public CourseResponseDTO createCourse(CourseRequestDTO course){
        Course newCourse = new Course();
        newCourse.setTitle(course.getTitle());
        newCourse.setDescription(course.getDescription());
        newCourse.setSection(course.getSection());
        newCourse.setCategory(course.getCategory());
        Course savedCourse = courseRepository.save(newCourse);
        
        CourseResponseDTO courseResponseDTO = modelMapper.map(savedCourse, CourseResponseDTO.class);
        // El curso recién creado no tiene instructor asignado
        courseResponseDTO.setInstructorName(null);
        courseResponseDTO.setInstructorLastname(null);
        courseResponseDTO.setInstructorEmail(null);
        courseResponseDTO.setInstructorProfilePhoto(null);
        
        setCourseStatistics(savedCourse, courseResponseDTO);
        
        return courseResponseDTO;
    }

    public void updateCourse(Long id, CourseRequestForUpdateDTO course){
        Course course1 = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Course not found"));
        if(course.getTitle() != null){
            course1.setTitle(course.getTitle());
        }
        if(course.getDescription() != null){
            course1.setDescription(course.getDescription());
        }
        if(course.getSection() != null){
            course1.setSection(course.getSection());
        }
        if(course.getCategory() != null){
            course1.setCategory(course.getCategory());
        }
        courseRepository.save(course1);
    }

    public List<CourseResponseDTO> getAllByTitle(String title){
        List<Course> courseList = courseRepository.findAllByTitle(title);
        return courseList.stream()
                .map(course -> {
                    CourseResponseDTO courseResponseDTO = modelMapper.map(course, CourseResponseDTO.class);
                    Instructor instructor = course.getInstructor();
                    courseResponseDTO.setInstructorName(instructor.getName());
                    courseResponseDTO.setInstructorLastname(instructor.getLastname());
                    courseResponseDTO.setInstructorEmail(instructor.getEmail());
                    courseResponseDTO.setInstructorProfilePhoto(instructor.getProfilePhoto());
                    
                    setCourseStatistics(course, courseResponseDTO);
                    
                    return courseResponseDTO;
                }).collect(Collectors.toList());
    }

    public CourseResponseDTO getCourseByTitleAndCategory(String title, String category){
        Course course = courseRepository.findCourseByTitleAndCategory(title, category).orElseThrow(() -> new ResourceNotFound("Course not found"));
        CourseResponseDTO courseResponseDTO = modelMapper.map(course, CourseResponseDTO.class);
        Instructor instructor = course.getInstructor();
        courseResponseDTO.setInstructorName(instructor.getName());
        courseResponseDTO.setInstructorLastname(instructor.getLastname());
        courseResponseDTO.setInstructorEmail(instructor.getEmail());
        courseResponseDTO.setInstructorProfilePhoto(instructor.getProfilePhoto());
        
        setCourseStatistics(course, courseResponseDTO);
        
        return courseResponseDTO;
    }

    public CourseResponseDTO getCourseByTitleAndSection(String title, String section){
        Course course = courseRepository.findCourseByTitleAndSection(title, section).orElseThrow(() -> new ResourceNotFound("Course not found"));
        CourseResponseDTO courseResponseDTO = modelMapper.map(course, CourseResponseDTO.class);
        Instructor instructor = course.getInstructor();
        courseResponseDTO.setInstructorName(instructor.getName());
        courseResponseDTO.setInstructorLastname(instructor.getLastname());
        courseResponseDTO.setInstructorEmail(instructor.getEmail());
        courseResponseDTO.setInstructorProfilePhoto(instructor.getProfilePhoto());
        
        setCourseStatistics(course, courseResponseDTO);
        
        return courseResponseDTO;
    }

    public void deleteCourse(Long id){
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Course not found"));
        courseRepository.delete(course);
    }

    public void assignInstructor(String email, Long id){
        Instructor instructor = instructorRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Teacher not found"));
        Course currentCourse = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Course Not Found"));
        currentCourse.setInstructor(instructor);
        instructor.getCourseList().add(currentCourse);
        courseRepository.save(currentCourse);
    }

    public List<CourseResponseDTO> getCoursesForCurrentUser() {
        User currentUser = userService.getAuthenticatedUser();

        List<Course> courses;

        if (currentUser instanceof Student student) {
            courses = student.getEnrollmentList().stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());
        } else if (currentUser instanceof Instructor instructor) {
            courses = courseRepository.findByInstructor(instructor);
        } else {
            throw new AccessDeniedException("Este usuario no tiene cursos asociados");
        }

        return courses.stream()
                .map(course -> {
                    CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
                    Instructor instructor = course.getInstructor();
                    dto.setInstructorName(instructor.getName());
                    dto.setInstructorLastname(instructor.getLastname());
                    dto.setInstructorEmail(instructor.getEmail());
                    dto.setInstructorProfilePhoto(instructor.getProfilePhoto());
                    
                    setCourseStatistics(course, dto);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(course -> {
                    CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
                    Instructor instructor = course.getInstructor();
                    if (instructor != null) {
                        dto.setInstructorName(instructor.getName());
                        dto.setInstructorLastname(instructor.getLastname());
                        dto.setInstructorEmail(instructor.getEmail());
                        dto.setInstructorProfilePhoto(instructor.getProfilePhoto());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void removeInstructor(Long courseId){
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFound("Course not found"));
        if (course.getInstructor() == null) {
            throw new ResourceNotFound("No instructor assigned to this course");
        }
        
        Instructor instructor = course.getInstructor();
        course.setInstructor(null);
        instructor.getCourseList().remove(course);
        
        courseRepository.save(course);
    }

//    private boolean isCurrentUserInstructorOrStudentOfCourse(Long courseId) {
//        // Verifica si es instructor del curso
//        try {
//            Instructor instructor = instructorService.getMe();
//            Course course = courseRepository.findById(courseId)
//                    .orElseThrow(() -> new ResourceNotFound("Course not found"));
//            if (course.getInstructor() != null && course.getInstructor().getId().equals(instructor.getId())) {
//                return true;
//            }
//        } catch (Exception ignored) {}
//
//        // Verifica si es estudiante inscrito en el curso
//        try {
//            Student student = studentService.getMe();
//            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
//            return enrollments.stream().anyMatch(e -> e.getStudent().getId().equals(student.getId()));
//        } catch (Exception ignored) {}
//
//        return false;
//    }

}
