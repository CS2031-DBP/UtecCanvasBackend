package com.example.gestiondecursos.Student.domain;

import com.example.gestiondecursos.Auth.utils.AuthorizationUtils;
import com.example.gestiondecursos.Student.Dto.StudentCreateDTO;
import com.example.gestiondecursos.Student.Dto.StudentRequestDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseForMeDTO;
import com.example.gestiondecursos.Student.infrastructure.StudentRepository;
import com.example.gestiondecursos.User.domain.Roles;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final PasswordEncoder passwordEncoder;

    public StudentResponseForMeDTO getStudentInfo(Long id){
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Student not found"));
        StudentResponseForMeDTO studentResponseDTO = modelMapper.map(student, StudentResponseForMeDTO.class);
        return studentResponseDTO;
    }

    public StudentResponseForMeDTO getMyOwnInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        if (!isStudent) {
            throw new AccessDeniedException("Only students can access this resource");
        }
        String username = authorizationUtils.getCurrentUser();
        if (username == null) {
            throw new ResourceNotFound("User not found");
        }
        Student student = studentRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("Student not found"));
        return getStudentInfo(student.getId());
    }


    public StudentResponseDTO createStudent(StudentCreateDTO student){
        Student student1 = new Student();
        student1.setName(student.getName());
        student1.setLastname(student.getLastname());
        if(studentRepository.findByEmail(student.getEmail()).isPresent()){
            throw new UserAlreadyExistException("Student already exists");
        }else{
            student1.setEmail(student.getEmail());
        }
        student1.setPassword(passwordEncoder.encode(student.getPassword()));
        //student1.setDescription(student.getDescription());
        //student1.setProfilePhoto(student.getProfilePhoto());
        student1.setRole(Roles.STUDENT);
        studentRepository.save(student1);
        StudentResponseDTO studentResponseDTO = modelMapper.map(student1, StudentResponseDTO.class);
        return studentResponseDTO;
    }

    public StudentResponseDTO getStudentByEmail(String email){
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Student not found"));
        StudentResponseDTO studentResponseDTO = modelMapper.map(student, StudentResponseDTO.class);
        return studentResponseDTO;
    }

    public List<StudentResponseDTO> getStudentsByName(String name){
        List<Student> studentList = studentRepository.findByName(name);
        List<StudentResponseDTO> studentResponseDTOS = studentList.stream().map(student -> modelMapper.map(student, StudentResponseDTO.class)).collect(Collectors.toList());
        return studentResponseDTOS;
    }

    public List<StudentResponseDTO> getStudentsByLastname(String lastname){
        List<Student> studentList = studentRepository.findByLastname(lastname);
        List<StudentResponseDTO> studentResponseDTOS = studentList.stream().map(student -> modelMapper.map(student, StudentResponseDTO.class)).collect(Collectors.toList());
        return studentResponseDTOS;
    }

    public StudentResponseDTO getByFullName(String name, String lastname){
        Student student = studentRepository.findByNameAndLastname(name, lastname).orElseThrow(() -> new ResourceNotFound("Student not found"));
        StudentResponseDTO studentResponseDTO = modelMapper.map(student, StudentResponseDTO.class);
        return studentResponseDTO;
    }

    public void deleteStudent(Long id){
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Student not found"));
        studentRepository.delete(student);
    }
    public void changeDescription(String description){
        StudentResponseForMeDTO studentResponseDTO = getMyOwnInfo();
        studentResponseDTO.setDescription(description);
        studentRepository.save(modelMapper.map(studentResponseDTO, Student.class));
    }

    public void changeProfilePicture(String profilePicture){
        StudentResponseForMeDTO studentResponseDTO = getMyOwnInfo();
        if(profilePicture != null){
            studentResponseDTO.setProfilePhoto(profilePicture);
        }
        studentRepository.save(modelMapper.map(studentResponseDTO, Student.class));
    }

    public Student getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        if (!isStudent) {
            throw new AccessDeniedException("Only students can access this resource");
        }
        String username = authorizationUtils.getCurrentUser();
        if (username == null) {
            throw new ResourceNotFound("User not found");
        }
        return studentRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("Student not found"));
    }

    public StudentResponseDTO updateStudent(String email, StudentRequestDTO student){ //Para Admins
        Student updatedStudent = studentRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Student not found"));
//        if(student.getDescription() != null){
//            updatedStudent.setDescription(student.getDescription());
//        }
//        if(student.getProfilePhoto() != null){
//            updatedStudent.setProfilePhoto(student.getProfilePhoto());
//        }
        if(student.getName() != null){
            updatedStudent.setName(student.getName());
        }
        if(student.getLastname() != null){
            updatedStudent.setLastname(student.getLastname());
        }
        studentRepository.save(updatedStudent);
        StudentResponseDTO responseDTO = modelMapper.map(updatedStudent, StudentResponseDTO.class);
        return responseDTO;
    }
    public List<StudentResponseDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(student -> modelMapper.map(student, StudentResponseDTO.class))
                .collect(Collectors.toList());
    }

}
