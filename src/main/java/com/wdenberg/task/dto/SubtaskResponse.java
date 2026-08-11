package com.wdenberg.task.dto;

import com.wdenberg.task.domain.model.Subtask;
import java.util.UUID;

public record SubtaskResponse(
        UUID id,
        String title,
        boolean completed
) {
    public static SubtaskResponse fromEntity(Subtask subtask) {
        return new SubtaskResponse(
                subtask.getId(),
                subtask.getTitle(),
                subtask.isCompleted()
        );
    }
}