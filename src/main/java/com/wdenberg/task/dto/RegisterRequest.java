package com.wdenberg.task.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (

        @NotBlank(message = "O nome é Obrigatório")
        String name,

        @NotBlank(message = "O E-mail é obrigatório")
        @Email(message = "Formato de e-mail invalido")
        String email,

        @NotBlank(message = "A senha é Obrigatória")
        @Size(min =  8, message = "A senha deve ter no minimo 8 caracteres")
        String password
){
}
