package com.wdenberg.task.domain.repository;

import com.wdenberg.task.domain.model.Task;
import com.wdenberg.task.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository  extends JpaRepository<Task, UUID> {

    // Busca Task por Status(Ex: trazer só as pendentes)
    List<Task> findByStatus(TaskStatus status);

    // Busca por palavras-chaves Ignorando M/m
    List<Task> findByTitleContainsIgnoreCase(String title);

    // Buascar Task Atrasadas
    List<Task> findByDueDateBeforeAndStatusNot(java.time.LocalDateTime now, TaskStatus status);
}
