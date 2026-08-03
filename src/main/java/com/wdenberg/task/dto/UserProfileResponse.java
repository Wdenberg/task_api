package com.wdenberg.task.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt
) {
}
