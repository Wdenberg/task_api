package com.wdenberg.service;


import com.wdenberg.domain.model.Task;
import com.wdenberg.domain.model.TaskStatus;
import com.wdenberg.domain.repository.TaskRepository;
import com.wdenberg.dto.TaskCreteRequest;
import com.wdenberg.dto.TaskResponse;
import com.wdenberg.dto.TaskUpdateRequest;
import com.wdenberg.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly= true)
public class TaskService {

    private  final TaskRepository taskRepository;

    public List<TaskResponse> finAll(){
        return taskRepository.findAll()
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse findById(UUID id){
        Task task = getTaskOrThrow(id);
        return TaskResponse.fromEntity(task);
    }

    public List<TaskResponse> findByStatus(TaskStatus status){
        return taskRepository.findByStatus(status)
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TaskResponse create(TaskCreteRequest request){
        Task task = Task.builder().title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .status(TaskStatus.PENDING).build();
        Task saveTask = taskRepository.save(task);
        return  TaskResponse.fromEntity(saveTask);
    }

    @Transactional
    public TaskResponse update(UUID id, TaskUpdateRequest request){
        Task task = getTaskOrThrow(id);

       task.update(request);

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

    private Task getTaskOrThrow(UUID id){
        return  taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
