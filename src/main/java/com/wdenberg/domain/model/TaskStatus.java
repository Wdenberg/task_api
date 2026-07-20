package com.wdenberg.domain.model;

public enum TaskStatus {
    PENDING("Pendente"),
    IN_PROGRESS("Em Andamento"),
    COMPLETED("Concluida"),
    CANCELLED("Cancelada");

    private final String description;

    TaskStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
