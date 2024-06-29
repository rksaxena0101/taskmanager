package com.rajat.taskmanager_spring_java.taskmanager.entity;

import lombok.Data;

@Data
public class NoteEntity {
    private int id;
    private String title;
    private String body;
}
