package com.rajat.taskmanager_spring_java.taskmanager.dtos;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TaskResponseDTO {
    private int id;
    private String title;
    private String description;
    private Date deadline;
    private boolean status;
    private List<NoteEntity> notes;
}
