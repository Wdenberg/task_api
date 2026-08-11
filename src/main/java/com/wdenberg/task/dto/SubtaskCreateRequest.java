package com.wdenberg.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubtaskCreateRequest(
        @NotBlank(message = "O título da subtarefa é obrigatório")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
        String title
) {}