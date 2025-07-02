package com.example.gestiondecursos.Student.application;

import com.example.gestiondecursos.Student.Dto.StudentCreateDTO;
import com.example.gestiondecursos.Student.Dto.StudentRequestDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseDTO;
import com.example.gestiondecursos.Student.Dto.StudentResponseForMeDTO;
import com.example.gestiondecursos.Student.domain.Student;
import com.example.gestiondecursos.Student.domain.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
     private final StudentService studentService;

    @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getByEmail/{email}")
     public ResponseEntity<StudentResponseDTO> getStudentByEmail(@PathVariable String email){
         StudentResponseDTO student = studentService.getStudentByEmail(email);
         return ResponseEntity.status(HttpStatus.OK).body(student);
     }

     @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getByName/{name}")
     public ResponseEntity<List<StudentResponseDTO>> getStudentByName(@PathVariable String name){
         List<StudentResponseDTO> studentList = studentService.getStudentsByName(name);
         return ResponseEntity.status(HttpStatus.OK).body(studentList);
     }

     @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getByLastname/{lastname}")
     public ResponseEntity<List<StudentResponseDTO>> getStudentByLastname(@PathVariable String lastname){
         List<StudentResponseDTO> studentList = studentService.getStudentsByLastname(lastname);
         return ResponseEntity.status(HttpStatus.OK).body(studentList);
     }

     @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getByFullName/name/{name}/lastname/{lastname}")
     public ResponseEntity<StudentResponseDTO> getByFullName(@PathVariable String name, @PathVariable String lastname){
         StudentResponseDTO student = studentService.getByFullName(name, lastname);
         return ResponseEntity.status(HttpStatus.OK).body(student);
     }

     @PreAuthorize("hasRole('STUDENT')")
     @PatchMapping("/changeDescription")
     public ResponseEntity<Void> changeDescription(@RequestBody String description){
        studentService.changeDescription(description);
        return ResponseEntity.noContent().build();
     }

    @PreAuthorize("hasRole('STUDENT')")
    @PatchMapping("/changePhoto")
    public ResponseEntity<Void> changePhoto(@RequestBody String photo){
        studentService.changeProfilePicture(photo);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
     @DeleteMapping("/getById/{id}")
     public ResponseEntity<Void> getStudentToDelete(@PathVariable Long id){
         studentService.deleteStudent(id);
         return ResponseEntity.noContent().build();
     }

     @PreAuthorize("hasRole('ADMIN')")
     @PatchMapping("/update/{email}")
     public ResponseEntity<Void> getStudentToUpdate(@PathVariable String email, @RequestBody @Valid StudentRequestDTO student){
         studentService.updateStudent(email, student);
         return ResponseEntity.noContent().build();
     }

     @PreAuthorize("hasRole('ADMIN')")
     @PostMapping
     public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody @Valid StudentCreateDTO student){
        StudentResponseDTO studentResponseDTO = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentResponseDTO);
     }

     @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getAllStudents")
     public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

     @PreAuthorize("hasRole('STUDENT')")
     @GetMapping("/getMyOwnInfo")
     public ResponseEntity<StudentResponseForMeDTO> getMy(){
        StudentResponseForMeDTO studentResponseDTO = studentService.getMyOwnInfo();
        return ResponseEntity.status(HttpStatus.OK).body(studentResponseDTO);
     }

}
