package com.rajat.taskmanager_spring_java.taskmanager.controller;

import com.rajat.taskmanager_spring_java.taskmanager.dtos.CreateNoteDTO;
import com.rajat.taskmanager_spring_java.taskmanager.dtos.CreateNoteResponseDTO;
import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import com.rajat.taskmanager_spring_java.taskmanager.service.NotesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/notes")
public class NotesController {

        private NotesService notesService;

    public NotesController(NotesService notesService) {
        this.notesService = notesService;
    }

    @GetMapping("")
    public ResponseEntity<List<NoteEntity>> getNotes(@PathVariable("taskId") long taskId) {
        var notes = notesService.getNotesForTask(taskId);
        return ResponseEntity.ok(notes);
    }

//    @GetMapping("")
//    public ResponseEntity<NoteEntity> getNotesByTaskId

    @PostMapping("")
    public ResponseEntity<CreateNoteResponseDTO> addNote(
            @PathVariable("taskId") Long taskId, @RequestBody CreateNoteDTO body) {
        var note = notesService.addNotesForTask(taskId, body.getTitle(), body.getBody());

        return ResponseEntity.ok(new CreateNoteResponseDTO(taskId, note));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable("taskId") long taskId, @PathVariable("noteId") long noteId) {
        notesService.deleteNoteById(taskId, noteId);
        return ResponseEntity.noContent().build();
    }
}
