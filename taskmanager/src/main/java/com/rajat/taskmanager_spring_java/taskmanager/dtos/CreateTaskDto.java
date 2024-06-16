package com.rajat.taskmanager_spring_java.taskmanager.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTaskDto {
    String title;
    String description;
    String deadline;

}
