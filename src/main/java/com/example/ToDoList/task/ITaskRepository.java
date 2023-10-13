package com.example.ToDoList.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByUserId(UUID userId);
    List<TaskModel> findAllByUserId(UUID userId);
}
