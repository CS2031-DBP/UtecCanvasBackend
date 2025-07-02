package com.example.gestiondecursos.Announcement.application;

import com.example.gestiondecursos.Announcement.domain.Announcement;
import com.example.gestiondecursos.Announcement.domain.AnnouncementService;
import com.example.gestiondecursos.Announcement.dto.AnnouncementRequestDTO;
import com.example.gestiondecursos.Announcement.dto.AnnouncementResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/course/{courseId}")
    public ResponseEntity<AnnouncementResponseDTO> createAnnouncement(@PathVariable Long courseId, @RequestBody AnnouncementRequestDTO announcementRequest) {
        AnnouncementResponseDTO response = announcementService.createAnnouncement(announcementRequest, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AnnouncementResponseDTO>> getAnnouncementsByCourse(@PathVariable Long courseId) {
        List<AnnouncementResponseDTO> announcements = announcementService.getAnnouncementsByCourse(courseId);
        return ResponseEntity.ok(announcements);
    }
}
