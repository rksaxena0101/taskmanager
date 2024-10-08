package com.rajat.taskmanager_spring_java.taskmanager.repository;

import com.rajat.taskmanager_spring_java.taskmanager.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("taskRepository")
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
