package com.wdenberg.task.domain.model;


import com.wdenberg.task.dto.TaskUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ttb_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // CallBacks de Ciclo de vida JPA

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        if(this.status == null){
            this.status = TaskStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public void update(TaskUpdateRequest request){

        if(request.title() != null && !request.title().isBlank()){
            this.title = request.title();
        }

        if(request.description() != null){
            this.description = request.description();
        }

        if(request.status() != null){
            this.status = request.status();
        }

        if(request.dueDate() != null){
            this.dueDate = request.dueDate();
        }
    }

}
