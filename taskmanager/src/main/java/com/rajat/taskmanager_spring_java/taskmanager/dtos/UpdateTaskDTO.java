package com.rajat.taskmanager_spring_java.taskmanager.dtos;

import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTaskDTO {
    String title;
    String description;
    String deadline;
    boolean status;
    User user;
}
