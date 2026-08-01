package com.wdenberg.task.controller;

import com.wdenberg.task.domain.model.User;
import com.wdenberg.task.domain.repository.UserRepository;
import com.wdenberg.task.dto.AuthenticationRequest;
import com.wdenberg.task.dto.LoginResponse;
import com.wdenberg.task.dto.RegisterRequest;
import com.wdenberg.task.infra.security.TokeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para Login e resgistro de Usuários")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private  final TokeService tokeService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest request){

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se a senha estiver correta, geramos o token JWT
        var token = tokeService.generateToken((User) auth.getPrincipal());
        return  ResponseEntity.ok(new LoginResponse(token));
    }


    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest registerRequest){
        if(this.userRepository.findByEmail(registerRequest.email()) != null){
            return ResponseEntity.badRequest().build();// Retorna 400 se o email ja extir

        }
        String encryptedPassword = passwordEncoder.encode(registerRequest.password());

        User newUser = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password(encryptedPassword)
                .build();

        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }


}
