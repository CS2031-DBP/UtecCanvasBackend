package com.example.gestiondecursos.Student.domain;

import com.example.gestiondecursos.Enrollment.domain.Enrollment;
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

@Table(name = "Student_table")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Student extends User {

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Enrollment> enrollmentList = new ArrayList<>();
}
