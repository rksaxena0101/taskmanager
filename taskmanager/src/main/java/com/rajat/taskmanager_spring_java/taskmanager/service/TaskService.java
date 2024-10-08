package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import com.rajat.taskmanager_spring_java.taskmanager.entity.User;
import com.rajat.taskmanager_spring_java.taskmanager.repository.TaskRepository;
import com.rajat.taskmanager_spring_java.taskmanager.repository.UserRepository;
import com.rajat.taskmanager_spring_java.taskmanager.security.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired  // Constructor-based dependency injection
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    private ArrayList<TaskEntity> tasks = new ArrayList<>();
    private final DateTimeFormatter deadlineDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
    private final DateTimeFormatter deadlineDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskEntity addTask(String title, String description, String deadline, boolean status) {
        TaskEntity task = new TaskEntity();
        long mostSignificantBits = UUID.randomUUID().getMostSignificantBits();
        long leastSignificantBits = UUID.randomUUID().getLeastSignificantBits();
        task.setId(Math.abs(mostSignificantBits ^ leastSignificantBits));
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
        System.out.println("TaskService::addTask:: "+SecurityContextHolder.getContext().getAuthentication());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication before if: " + authentication);
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("User is not authenticated");
        }

        //String username = authentication.getName();
        User user = (User) authentication.getPrincipal();
        System.out.println("user from authentication: " + user);
        //User user = userRepository.findByEmail(username);
        System.out.println("TaskService::addTask User fetched from DB: " + user);

        if (user == null) {
            throw new RuntimeException("User not found");
        }
        task.setUser(user);  // Set the entire User entity, not just the userId
        tasks.add(task);
        return taskRepository.save(task);  // Ensure task is persisted in the DB;
    }

    public List<TaskEntity> getTasks() {
        return taskRepository.findAll();  // Fetch all tasks from DB
    }

    public TaskEntity getTaskById(long id) {
        TaskEntity taskFromDB = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskFromDB;
    }

    public TaskEntity updateTask(long id, String title, String description, String deadline, Boolean status) {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        String username = authentication.getName();
        System.out.println("Username from authentication: " + username);
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }
        task.setUser(user);
        return taskRepository.save(task);  // Ensure task is persisted in the DB
    }

    public TaskEntity deleteTaskById(long id) {
        TaskEntity task = getTaskById(id);
        if (task == null) {
            return null;
        }
        tasks.remove(task);
        System.out.println("TaskService::deleteTaskById:: "+task);
        taskRepository.delete(task); // Delete the task through the repository
        return task;
    }
}