package com.rajat.taskmanager_spring_java.taskmanager.controller;

import com.rajat.taskmanager_spring_java.taskmanager.dtos.CreateTaskDto;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.ErrorResponseDTO;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.TaskResponseDTO;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.UpdateTaskDTO;
import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import com.rajat.taskmanager_spring_java.taskmanager.service.NotesService;
import com.rajat.taskmanager_spring_java.taskmanager.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final NotesService notesService;
    private final ModelMapper modelMapper;

    public TaskController(TaskService taskService, NotesService notesService) {
        this.taskService = taskService;
        this.notesService = notesService;
        this.modelMapper = new ModelMapper();
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<TaskEntity>> getTasks() {
        var tasks = taskService.getTasks();
        return (tasks == null) ? null :ResponseEntity.ok(tasks);
    }

    @GetMapping("{id}")
    @ResponseBody
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable("id") Integer id) {
        var task = taskService.getTaskById(id);
        var notes = notesService.getNotesForTask(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        var taskResponse = modelMapper.map(task, TaskResponseDTO.class);
        taskResponse.setNotes(notes);
        return ResponseEntity.ok(taskResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @ResponseBody
    public ResponseEntity<TaskEntity> addTask(@RequestBody CreateTaskDto createTaskDto) throws ParseException {
        var task = taskService.addTask(createTaskDto.getTitle(), createTaskDto.getDescription(), createTaskDto.getDeadline(), createTaskDto.isStatus());
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseBody
    public ResponseEntity<TaskEntity> updateTask(@PathVariable("id") Integer id, @RequestBody UpdateTaskDTO updateTaskDto) throws ParseException {
        var task = taskService.updateTask(id, updateTaskDto.getTitle(), updateTaskDto.getDescription(), updateTaskDto.getDeadline(), updateTaskDto.isStatus());
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseBody
    public ResponseEntity<TaskEntity> deleteTaskById(@PathVariable("id") Integer id) {
        var task = taskService.deleteTaskById(id);
        return ResponseEntity.ok(task);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponseDTO> handleErrors(Exception e) {
        if (e instanceof ParseException) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Invalid date format."));
        }
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(new ErrorResponseDTO("Internal Server Error"));
    }

}