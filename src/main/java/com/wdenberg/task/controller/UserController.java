package com.wdenberg.task.controller;

import com.wdenberg.task.domain.model.User;
import com.wdenberg.task.domain.repository.UserRepository;
import com.wdenberg.task.dto.UpdateProfileRequest;
import com.wdenberg.task.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de dados do usuário")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal User user){



        if(user == null){
            return ResponseEntity.notFound().build();
        }

      UserProfileResponse response = new UserProfileResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt()
        );

        return ResponseEntity.ok(response);

    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @jakarta.validation.Valid UpdateProfileRequest request
    ) {
        // Verifica se o novo e-mail já pertence a outro usuário cadastrado no banco
        if (!user.getEmail().equalsIgnoreCase(request.email())) {
            var existingUser = userRepository.findByEmail(request.email());
            if (existingUser != null) {
                return ResponseEntity.badRequest().build(); // Retorna 400 se o e-mail já estiver em uso
            }
        }

        // Atualiza os dados básicos
        user.setName(request.name());
        user.setEmail(request.email());

        // Se o usuário enviou uma nova senha, faz o encode e atualiza
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        // Salva as alterações no banco de dados
        userRepository.save(user);

        // Retorna o perfil atualizado
        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}

