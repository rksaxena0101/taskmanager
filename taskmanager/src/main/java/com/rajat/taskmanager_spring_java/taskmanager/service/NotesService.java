package com.rajat.taskmanager_spring_java.taskmanager.service;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class NotesService {

    private TaskService taskService;
    private HashMap<Integer, TaskNotesHandler> taskNotesHandler = new HashMap<>();

    public NotesService(TaskService taskService) {
        this.taskService = taskService;
    }
    class TaskNotesHandler {
        protected int noteId = 1;
        protected ArrayList<NoteEntity> notes = new ArrayList<>();
    }

    public List<NoteEntity> getNotesForTask(int taskId) {
        TaskEntity task = taskService.getTaskById(taskId);
        if (task == null) {
            return null;
        }
        if(taskNotesHandler.get(taskId) == null) {
            taskNotesHandler.put(taskId, new TaskNotesHandler());
        }
        return taskNotesHandler.get(taskId).notes;
    }

    public NoteEntity addNotesForTask(int taskId, String title, String body) {
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

    public NoteEntity getNoteById(int noteId) {
        TaskNotesHandler taskNoteHandler2 = new TaskNotesHandler();
       for(NoteEntity notes: taskNoteHandler2.notes) {
           if(notes.getId() == noteId){
               return notes;
           }
       }
        return null;
    }
    public void deleteNoteById(int taskId, int noteId) {
        TaskNotesHandler taskNotesHandler = this.taskNotesHandler.get(taskId);
        if (taskNotesHandler == null) {
            return;
        }

        taskNotesHandler.notes.removeIf(note -> note.getId() == noteId);
    }
}
