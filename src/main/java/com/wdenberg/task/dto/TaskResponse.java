package com.wdenberg.task.dto;

import com.wdenberg.task.domain.model.Task;
import com.wdenberg.task.domain.model.TaskPriority;
import com.wdenberg.task.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<SubtaskResponse> subtasks
) {
    public static TaskResponse fromEntity(Task task) {
        List<SubtaskResponse> subtaskResponses = task.getSubtasks() != null
                ? task.getSubtasks().stream().map(SubtaskResponse::fromEntity).toList()
                : List.of();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                subtaskResponses
        );
    }
}