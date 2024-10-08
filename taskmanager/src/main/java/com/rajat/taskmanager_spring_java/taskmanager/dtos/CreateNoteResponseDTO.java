package com.rajat.taskmanager_spring_java.taskmanager.dtos;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateNoteResponseDTO {
    private long taskId;
    private NoteEntity note;
}
