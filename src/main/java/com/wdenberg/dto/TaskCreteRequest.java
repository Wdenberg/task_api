package com.wdenberg.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskCreteRequest(

        @NotBlank(message = "O Titulo é Obrigatorio")
        @Size(min = 3, max = 100, message = "O Titulo deve ter entre 3 e 100 Caracteres.")
        String title,

        @Size(max = 500, message = "A descrição não pode exceder mais de 500 caracteres")
        String description,

        @FutureOrPresent(message = "A Data de conclusão deve ser no presente ou no futuro")
        LocalDateTime dueDate
) { }
