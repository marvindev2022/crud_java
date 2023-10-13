package com.example.ToDoList.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be after current date");
        }
        if (taskModel.getEndAt() != null && taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date must be after start date");
        }
        if (userId != null) {
            taskModel.setUserId(userId);
            TaskModel taskCreated = this.taskRepository.save(taskModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @GetMapping("/")
    public ResponseEntity getAll(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId != null) {
            List<TaskModel> tasks = this.taskRepository.findAllByUserId(userId);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }
    @GetMapping("/{taskId}")
    public ResponseEntity getOne(@PathVariable UUID taskId, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId != null) {
            TaskModel task = this.taskRepository.findByUserId(userId).stream().filter(t -> t.getId().equals(taskId))
                    .findFirst().orElse(null);
            if (task != null) {
                return ResponseEntity.status(HttpStatus.OK).body(task);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity update(@PathVariable UUID taskId, @RequestBody TaskModel taskModel,
            HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be after current date");
        }
        if (taskModel.getEndAt() != null && taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date must be after start date");
        }
        if (userId != null) {
            TaskModel task = this.taskRepository.findByUserId(userId).stream().filter(t -> t.getId().equals(taskId))
                    .findFirst().orElse(null);
            if (task != null) {
                task.setTitle(taskModel.getTitle());
                task.setDescription(taskModel.getDescription());
                task.setStartAt(taskModel.getStartAt());
                task.setEndAt(taskModel.getEndAt());
                task.setUpdatedAt(LocalDateTime.now());
                TaskModel taskUpdated = this.taskRepository.save(task);
                return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity delete(@PathVariable UUID taskId, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId != null) {
            TaskModel task = this.taskRepository.findByUserId(userId).stream().filter(t -> t.getId().equals(taskId))
                    .findFirst().orElse(null);
            if (task != null) {
                this.taskRepository.delete(task);
                return ResponseEntity.status(HttpStatus.OK).body("Task deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }

}
