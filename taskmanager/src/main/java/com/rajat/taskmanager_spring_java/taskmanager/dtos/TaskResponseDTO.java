package com.rajat.taskmanager_spring_java.taskmanager.dtos;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TaskResponseDTO {
    private long id;
    private String title;
    private String description;
    private String deadline;
    private boolean status;
    private List<NoteEntity> notes;
}
