package com.wdenberg.task.service;


import com.wdenberg.task.domain.model.Task;
import com.wdenberg.task.domain.model.TaskStatus;
import com.wdenberg.task.domain.model.User;
import com.wdenberg.task.domain.repository.TaskRepository;
import com.wdenberg.task.dto.TaskCreteRequest;
import com.wdenberg.task.dto.TaskResponse;
import com.wdenberg.task.dto.TaskUpdateRequest;
import com.wdenberg.task.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly= true)
public class TaskService {

    private  final TaskRepository taskRepository;

    // --- MÉTODOS DE SEGURANÇA E AUXILIARES ---

    // Extrai o usuário logado diretamente do contexto do Spring Security
    private User getAuthenticationUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // Busca a tarefa garantindo que ela pertence ao usuário logado!
    private Task getTaskOrThrow(UUID taskId){
        User user = getAuthenticationUser();
        return taskRepository.findByIdAndUserId(taskId, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }


    // --- REGRAS DE NEGÓCIO ---

    public List<TaskResponse> finAll(){
        User user = getAuthenticationUser();
        return taskRepository.findAllByUserId(user.getId())
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse findById(UUID id){
        Task task = getTaskOrThrow(id);
        return TaskResponse.fromEntity(task);
    }

    public List<TaskResponse> findByStatus(TaskStatus status){
        User user = getAuthenticationUser();
        return taskRepository.findByUserIdAndStatus(user.getId(), status)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TaskResponse create(TaskCreteRequest request){
        User user = getAuthenticationUser();
        Task task = Task.builder().title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(TaskStatus.PENDING)
                .user(user)
                .build();
        Task saveTask = taskRepository.save(task);
        return  TaskResponse.fromEntity(saveTask);
    }

    @Transactional
    public TaskResponse update(UUID id, TaskUpdateRequest request) {
        Task task = getTaskOrThrow(id);

        if (request.title() != null && !request.title().isBlank()) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.status() != null) task.setStatus(request.status());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void  delete(UUID id){
        Task task = getTaskOrThrow(id);
        taskRepository.delete(task);
    }

    @Transactional
    public TaskResponse completeTask(UUID id){
        Task task = getTaskOrThrow(id);
        task.setStatus(TaskStatus.COMPLETED);
        return  TaskResponse.fromEntity(task);
    }

    /*
    private Task getTaskOrThrow(UUID id){
        return  taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

     */
}
