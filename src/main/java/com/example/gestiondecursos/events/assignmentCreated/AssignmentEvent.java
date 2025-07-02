package com.example.gestiondecursos.events.assignmentCreated;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AssignmentEvent extends ApplicationEvent {
    private final String title;
    private final Integer maxScore;
    private final String dueDate;

    public AssignmentEvent(Object source, String title, Integer maxScore, String dueDate){
        super(source);
        this.title = title;
        this.maxScore = maxScore;
        this.dueDate = dueDate;
    }
}
