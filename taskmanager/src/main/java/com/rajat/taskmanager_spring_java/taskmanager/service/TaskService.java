package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class TaskService {
    private ArrayList<TaskEntity> tasks = new ArrayList<>();
    private int taskId = 1;
    private final SimpleDateFormat deadlineDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public TaskEntity addTask(String title, String description, String deadline) throws ParseException {
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadlineDateFormatter.parse(deadline));
        task.setStatus(false);
        tasks.add(task);
        taskId++;
        return task;
    }

    public ArrayList<TaskEntity> getTasks() { return tasks; }

    public TaskEntity getTaskById(int id) {
        for(TaskEntity task: tasks) {
            if(task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public TaskEntity updateTask(int id, String description, String deadline, Boolean status) throws ParseException {
        TaskEntity task = getTaskById(id);
        if(task == null) {
            return null;
        }
        if(description != null){
            task.setDescription(description);
        }
        if(deadline != null) {
            task.setDeadline(deadlineDateFormatter.parse(deadline));
        }
        if(status != null) {
            task.setStatus(status);
        }
        return task;
    }
}
