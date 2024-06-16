package com.rajat.taskmanager_spring_java.taskmanager.controller;

import com.rajat.taskmanager_spring_java.taskmanager.dtos.CreateTaskDto;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.ErrorResponseDTO;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.UpdateTaskDTO;
import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import com.rajat.taskmanager_spring_java.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @GetMapping("")
    public ResponseEntity<List<TaskEntity>> getTasks() {
        var tasks = taskService.getTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("{id}")
    public ResponseEntity<TaskEntity> getTaskById(@PathVariable("id") Integer id) {
        var task = taskService.getTaskById(id);
        if(task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping("")
    public ResponseEntity<TaskEntity> addTask(@RequestBody CreateTaskDto createTaskDto) throws ParseException {
        var task = taskService.addTask(createTaskDto.getTitle(), createTaskDto.getDescription(), createTaskDto.getDeadline());
        return ResponseEntity.ok(task);
    }

    @PatchMapping("{id}")
    public ResponseEntity<TaskEntity> updateTask(@PathVariable("id") Integer id, @RequestBody UpdateTaskDTO updateTaskDto) throws ParseException {
        var task = taskService.updateTask(id, updateTaskDto.getDescription()
                    ,updateTaskDto.getDeadline(),updateTaskDto.getStatus());
        if(task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleErrors(Exception e) {
        if(e instanceof ParseException) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid date format."));
        }
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponseDTO("Internal Server Error"));
    }

}
