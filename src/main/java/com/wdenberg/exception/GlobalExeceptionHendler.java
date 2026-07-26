package com.wdenberg.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExeceptionHendler  {

    public record ErroResponse(
            LocalDateTime timestamp,
            int staus,
            String error,
            Object message
    ){ }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErroResponse> hendeleTaskNotFund(TaskNotFoundException exception){
        ErroResponse error  = new ErroResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> hendleValidationErros(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ErroResponse error = new ErroResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

/*
{
    "timestamp": "2025-07-20T15:30:20",
    "status": 404,
    "error": "Not Found",
    "message": "Task não encontrada"
}

{
    "timestamp": "2025-07-20T15:40:22",
    "status": 400,
    "error": "Validation Error",
    "message": {
        "titulo": "não pode estar em branco",
        "prioridade": "não pode ser nulo"
    }
}

 */