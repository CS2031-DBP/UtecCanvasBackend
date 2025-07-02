package com.example.gestiondecursos.Announcement.infrastructure;

import com.example.gestiondecursos.Announcement.domain.Announcement;
import com.example.gestiondecursos.Course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseOrderByCreatedAtDesc(Course course);
}
