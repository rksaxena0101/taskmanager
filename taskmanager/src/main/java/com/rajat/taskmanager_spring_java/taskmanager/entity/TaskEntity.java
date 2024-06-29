package com.rajat.taskmanager_spring_java.taskmanager.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TaskEntity {
    private int id;
    private String title;
    private String description;
    private Date deadline;
    private boolean status;
    //private List<NoteEntity> notes;

}
