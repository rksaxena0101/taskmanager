package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import com.rajat.taskmanager_spring_java.taskmanager.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NotesService {

    private NoteRepository noteRepository;
    private TaskService taskService;
    private HashMap<Long, TaskNotesHandler> taskNotesHandler = new HashMap<>();

    public NotesService(TaskService taskService, NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
        this.taskService = taskService;
    }
    class TaskNotesHandler {
        protected int noteId = 1;
        protected ArrayList<NoteEntity> notes = new ArrayList<>();
    }

    public List<NoteEntity> getNotesForTask(long taskId) {
        TaskEntity task = taskService.getTaskById(taskId);
        if (task == null) {
            return null;
        }
        if(taskNotesHandler.get(taskId) == null) {
            taskNotesHandler.put(taskId, new TaskNotesHandler());
        }
        return taskNotesHandler.get(taskId).notes;
    }

    public NoteEntity addNotesForTask(long taskId, String title, String body) {
        TaskEntity task = taskService.getTaskById(taskId);
        if(task == null) {
            return null;
        }
        if(taskNotesHandler.get(taskId) == null) {
            taskNotesHandler.put(taskId, new TaskNotesHandler());
        }

        TaskNotesHandler taskNotesHandler1 = taskNotesHandler.get(taskId);
        NoteEntity note = new NoteEntity();
        note.setId(taskNotesHandler1.noteId);
        note.setTitle(title);
        note.setBody(body);
        taskNotesHandler1.notes.add(note);
        taskNotesHandler1.noteId++;
        return note;
    }

    public NoteEntity getNoteById(long noteId) {
        TaskNotesHandler taskNoteHandler2 = new TaskNotesHandler();
       for(NoteEntity notes: taskNoteHandler2.notes) {
           if(notes.getId() == noteId){
               return notes;
           }
       }
        return null;
    }
    public void deleteNoteById(long taskId, long noteId) {
        TaskNotesHandler taskNotesHandler = this.taskNotesHandler.get(taskId);
        if (taskNotesHandler == null) {
            return;
        }

        taskNotesHandler.notes.removeIf(note -> note.getId() == noteId);
    }
}
