package com.wdenberg.exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID id){
        super("Tarefa não encontrada para o ID fornecido: " + id);
    }
}
