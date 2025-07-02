package com.example.gestiondecursos.Instructor.application;

import com.example.gestiondecursos.Instructor.domain.Instructor;
import com.example.gestiondecursos.Instructor.domain.InstructorService;
import com.example.gestiondecursos.Instructor.dto.InstructorCreatedDTO;
import com.example.gestiondecursos.Instructor.dto.InstructorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<InstructorResponseDTO> createInstructor(@RequestBody @Valid InstructorCreatedDTO instructor){
        InstructorResponseDTO instructorResponseDTO = instructorService.createInstructor(instructor);
        return ResponseEntity.status(HttpStatus.CREATED).body(instructorResponseDTO);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping
    public ResponseEntity<InstructorResponseDTO> getInstructorInfo(){
        InstructorResponseDTO instructorResponseDTO = instructorService.getInstructorOwnInfo();
        return ResponseEntity.status(HttpStatus.OK).body(instructorResponseDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<List<InstructorResponseDTO>> allInstructors(){
        List<InstructorResponseDTO> instructor = instructorService.getAllInstructors();
        return ResponseEntity.status(HttpStatus.OK).body(instructor);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/name/{name}/lastname/{lastname}")
    public ResponseEntity<InstructorResponseDTO> getByFullName(@PathVariable String name, @PathVariable String lastname){
        InstructorResponseDTO instructor = instructorService.getInstructorByNameAndLastname(name, lastname);
        return ResponseEntity.status(HttpStatus.OK).body(instructor);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/getAllByName/{name}")
    public ResponseEntity<List<InstructorResponseDTO>> getByName(@PathVariable String name){
        List<InstructorResponseDTO> instructors = instructorService.getInstructorsByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(instructors);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<InstructorResponseDTO> getByEmail(@PathVariable String email){
        InstructorResponseDTO instructor = instructorService.getByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(instructor);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/instructorId/{id}")
    public ResponseEntity<Void> updateInstructor(@PathVariable Long id, @RequestBody String photo){
        instructorService.updateInstructor(id, photo);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long id){
        instructorService.deleteInstructor(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PatchMapping("/changePhoto")
    public ResponseEntity<Void> changePhoto(@RequestBody String photo){
        instructorService.changeProfilePicture(photo);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PatchMapping("/changeDescription")
    public ResponseEntity<Void> changeDescription(@RequestBody String description){
        instructorService.changeDescription(description);
        return ResponseEntity.noContent().build();
    }

}
