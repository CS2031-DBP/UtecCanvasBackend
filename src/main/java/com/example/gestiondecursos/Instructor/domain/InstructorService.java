package com.example.gestiondecursos.Instructor.domain;

import com.example.gestiondecursos.Auth.utils.AuthorizationUtils;
import com.example.gestiondecursos.Instructor.dto.InstructorCreatedDTO;
import com.example.gestiondecursos.Instructor.dto.InstructorResponseDTO;
import com.example.gestiondecursos.Instructor.infrastructure.InstructorRepository;
import com.example.gestiondecursos.User.domain.Roles;
import com.example.gestiondecursos.User.domain.User;
import com.example.gestiondecursos.exceptions.ResourceIsNullException;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import com.example.gestiondecursos.exceptions.UserAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository instructorRepository;
    private final ModelMapper modelMapper;
    private final AuthorizationUtils authorizationUtils;
    private final PasswordEncoder passwordEncoder;

    public InstructorResponseDTO getInstructorInfo(Long id){
        Instructor instructor = instructorRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Instructor not found"));;
        InstructorResponseDTO instructorResponseDTO = modelMapper.map(instructor, InstructorResponseDTO.class);
        return instructorResponseDTO;
    }

    private Instructor byEmail(String email){
        return instructorRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Instructor not found"));
    }

    public Instructor getMe(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceIsNullException("User not authenticated");
        }
        boolean isInstructor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        if(!isInstructor) {
            throw new AccessDeniedException("Only Instructors can access this resource");
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        Instructor instructor = byEmail(email);
        return instructor;
    }

    public InstructorResponseDTO createInstructor(InstructorCreatedDTO instructor){
        Instructor instructor1 = new Instructor();
        instructor1.setName(instructor.getName());
        instructor1.setLastname(instructor.getLastname());
        //instructorRepository.findByEmail(instructor.getEmail()).orElseThrow(() -> new UserAlreadyExistException("Email Already exists"));
        //Falta logica para evitar crear otro profe
        if((instructorRepository.findByEmail(instructor.getEmail()).isPresent())){
            throw new UserAlreadyExistException("Instructor Already exists");
        }else{
            instructor1.setEmail(instructor.getEmail());
        }
        instructor1.setPassword(passwordEncoder.encode(instructor.getPassword()));
        //instructor1.setDescription(instructor.getDescription());
        //instructor1.setProfilePhoto(instructor.getProfilePhoto());
        instructor1.setRole(Roles.INSTRUCTOR);
        instructorRepository.save(instructor1);
        InstructorResponseDTO instructorResponseDTO = modelMapper.map(instructor1, InstructorResponseDTO.class);
        return instructorResponseDTO;
    }

    public InstructorResponseDTO getInstructorOwnInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isInstructor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        if (!isInstructor) {
            throw new AccessDeniedException("Only instructors can access this resource");
        }

        String username = authorizationUtils.getCurrentUser();
        if (username == null) {
            throw new ResourceNotFound("User not found");
        }

        Instructor instructor = instructorRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFound("Instructor not found"));

        return getInstructorInfo(instructor.getId());
    }


    public List<InstructorResponseDTO> getAllInstructors(){
        List<Instructor> instructors = instructorRepository.findAll();
        List<InstructorResponseDTO> instructorResponseDTOS = instructors.stream().map(instructor -> modelMapper.map(instructor, InstructorResponseDTO.class)).collect(Collectors.toList());
        return instructorResponseDTOS;
    }

    public InstructorResponseDTO getInstructorByNameAndLastname(String name, String lastname){
        Instructor instructor = instructorRepository.findByNameAndLastname(name, lastname).orElseThrow(() -> new ResourceNotFound("Instructor not found"));
        InstructorResponseDTO instructorResponseDTO = modelMapper.map(instructor, InstructorResponseDTO.class);
        return instructorResponseDTO;
    }

    public List<InstructorResponseDTO> getInstructorsByName(String name){
        List<Instructor> instructors = instructorRepository.findByName(name);
        List<InstructorResponseDTO> instructorResponseDTOS = instructors.stream().map(instructor -> modelMapper.map(instructor, InstructorResponseDTO.class)).collect(Collectors.toList());
        return instructorResponseDTOS;
    }

    public InstructorResponseDTO getByEmail(String email){
        Instructor instructor = instructorRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Instructor not found"));
        InstructorResponseDTO instructorResponseDTO = modelMapper.map(instructor, InstructorResponseDTO.class);
        return instructorResponseDTO;
    }

    public void changeProfilePicture(String photo){
        Instructor instructor = getMe();
        if(photo != null){
            instructor.setProfilePhoto(photo);
        }
        instructorRepository.save(instructor);
    }

    public void changeDescription(String description){
        Instructor instructor = getMe();
        if(description != null){
            instructor.setDescription(description);
        }
        instructorRepository.save(instructor);
    }

    public void updateInstructor(Long id, String profilePhoto){ //Admin
        Instructor instructor1 = instructorRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Instructor not found"));

        if(profilePhoto != null){
            instructor1.setProfilePhoto(profilePhoto);
        }
        instructorRepository.save(instructor1);
    }

    public void deleteInstructor(Long id){
        Instructor instructor1 = instructorRepository.findById(id).orElseThrow(() -> new ResourceNotFound("Instructor not found"));
        instructorRepository.delete(instructor1);
    }
}
