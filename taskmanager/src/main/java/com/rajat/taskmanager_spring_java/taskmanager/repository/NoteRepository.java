package com.rajat.taskmanager_spring_java.taskmanager.repository;

import com.rajat.taskmanager_spring_java.taskmanager.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("noteRepository")
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
}
