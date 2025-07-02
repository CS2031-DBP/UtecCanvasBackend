package com.example.gestiondecursos.Instructor.domain;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.User.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Instructor_table")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Instructor extends User {

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
    private List<Course> courseList = new ArrayList<>();
}
