package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Service
public class TaskService {
    private ArrayList<TaskEntity> tasks = new ArrayList<>();
    private int taskId = 1;
    private final DateTimeFormatter deadlineDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private final DateTimeFormatter deadlineDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskEntity addTask(String title, String description, String deadline, boolean status) {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);

        try {
            ZonedDateTime zonedDeadline;
            try {
                // Try to parse with both date and time
                zonedDeadline = ZonedDateTime.parse(deadline, deadlineDateTimeFormatter);
            } catch (DateTimeParseException e) {
                // If parsing fails, fallback to date only and set time to start of the day
                zonedDeadline = LocalDate.parse(deadline, deadlineDateFormatter).atStartOfDay(ZoneId.systemDefault());
            }
            task.setDeadline(zonedDeadline);
        } catch (DateTimeParseException e) {
            // Handle the error - maybe set the deadline to a default value or throw an exception
            e.printStackTrace();
            task.setDeadline(null); // or handle it another way
        }

        task.setStatus(status);
        tasks.add(task);
        taskId++;
        return task;
    }

    public ArrayList<TaskEntity> getTasks() { return tasks; }

    public TaskEntity getTaskById(int id) {
        for (TaskEntity task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public TaskEntity updateTask(int id, String title, String description, String deadline, Boolean status) {
        TaskEntity task = getTaskById(id);
        if (task == null) {
            return null;
        }
        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (deadline != null) {
            try {
                ZonedDateTime zonedDeadline;
                try {
                    // Try to parse with both date and time
                    zonedDeadline = ZonedDateTime.parse(deadline, deadlineDateTimeFormatter);
                } catch (DateTimeParseException e) {
                    // If parsing fails, fallback to date only and set time to start of the day
                    zonedDeadline = LocalDate.parse(deadline, deadlineDateFormatter).atStartOfDay(ZoneId.systemDefault());
                }
                task.setDeadline(zonedDeadline);
            } catch (DateTimeParseException e) {
                // Handle the error - maybe set the deadline to a default value or throw an exception
                e.printStackTrace();
                task.setDeadline(null); // or handle it another way
            }
        }
        if (status != null) {
            task.setStatus(status);
        }
        return task;
    }

    public TaskEntity deleteTaskById(int id) {
        TaskEntity task = getTaskById(id);
        if (task == null) {
            return null;
        }
        tasks.remove(task);
        return task;
    }
}