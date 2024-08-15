package com.rajat.taskmanager_spring_java.taskmanager.entity;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Date;


@Data
public class TaskEntity {
    private int id;
    private String title;
    private String description;
    private ZonedDateTime deadline;;
    private boolean status;
    //private List<NoteEntity> notes;

}
