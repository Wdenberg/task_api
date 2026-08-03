package com.wdenberg.task.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        // A senha é opcional na atualização (caso o usuário queira trocar)
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password
) {
}
