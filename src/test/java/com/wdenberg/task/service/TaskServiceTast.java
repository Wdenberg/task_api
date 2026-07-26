package com.wdenberg.task.service;


import com.wdenberg.task.domain.model.Task;
import com.wdenberg.task.domain.model.TaskStatus;
import com.wdenberg.task.domain.repository.TaskRepository;
import com.wdenberg.task.dto.TaskCreteRequest;
import com.wdenberg.task.dto.TaskResponse;
import com.wdenberg.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTast {

    @Mock
    private TaskRepository taskRepository;

   @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Deve Cria uma Nova Tarefa Com Sucesso com status PENDING")
    void shouldCreateTaskSuccessfully(){
        TaskCreteRequest taskCreteRequest = new TaskCreteRequest(
                "Estudando TestContainer",
                "Criando Testes de Integração",
                LocalDateTime.now().plusDays(2)
        );

        Task saveTask = Task.builder()
                .id(UUID.randomUUID())
                .title(taskCreteRequest.title())
                .description(taskCreteRequest.description())
                .dueDate(taskCreteRequest.dueDate())
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(saveTask);

        TaskResponse response = taskService.create(taskCreteRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(saveTask.getId());
        assertThat(response.title()).isEqualTo("Estudando TestContainers");
        assertThat(response.status()).isEqualTo(TaskStatus.PENDING);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve Lançar TaskNotFoundException ao Buscar por ID inexistente")
    void  shouldThrowExceptionWhenTaskNotFound(){
        UUID randomId = UUID.randomUUID();
        when(taskRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(randomId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Tarefa Não Encontrada para o ID fornecido: " + randomId);

        verify(taskRepository, times(1)).findById(randomId);
    }

    @Test
    @DisplayName("Deve Alterar o status para COMPLETED ao concluir Tarefa")
    void shouldCompleteTaskSuccessfuly(){
        UUID taskId = UUID.randomUUID();

        Task existingTask = Task.builder()
                .id(taskId)
                .title("Tarefa Pendente")
                .status(TaskStatus.PENDING)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // ACT
        TaskResponse response = taskService.completeTask(taskId);

        // Assert
        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository, times(1)).findById(taskId);
    }
}
