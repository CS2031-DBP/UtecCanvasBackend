package com.example.gestiondecursos.User.application;

import com.example.gestiondecursos.User.domain.UserService;
import com.example.gestiondecursos.User.dto.UserResponseDTO;
import com.example.gestiondecursos.User.dto.UpdateProfilePhotoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(){
        UserResponseDTO currentUser = userService.getMe();
        return ResponseEntity.ok(currentUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/profile-photo")
    public ResponseEntity<UserResponseDTO> updateProfilePhoto(@RequestBody UpdateProfilePhotoDTO updateDTO){
        UserResponseDTO updatedUser = userService.updateProfilePhoto(updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
