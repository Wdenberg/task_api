package com.wdenberg.task.service;

import com.wdenberg.task.domain.model.Task;
import com.wdenberg.task.domain.model.TaskPriority;
import com.wdenberg.task.domain.model.TaskStatus;
import com.wdenberg.task.domain.model.User;
import com.wdenberg.task.domain.repository.TaskRepository;
import com.wdenberg.task.dto.TaskCreteRequest;
import com.wdenberg.task.dto.TaskResponse;
import com.wdenberg.task.exception.TaskNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(UUID.randomUUID())
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve Criar uma Nova Tarefa Com Sucesso com status PENDING")
    void shouldCreateTaskSuccessfully(){
        TaskCreteRequest taskCreteRequest = new TaskCreteRequest(
                "Estudando TestContainer",
                "Criando Testes de Integração",
                TaskPriority.MEDIUM,
                LocalDateTime.now().plusDays(2)
        );

        Task saveTask = Task.builder()
                .id(UUID.randomUUID())
                .title(taskCreteRequest.title())
                .description(taskCreteRequest.description())
                .dueDate(taskCreteRequest.dueDate())
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .user(mockUser)
                .createdAt(LocalDateTime.now())
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(saveTask);

        TaskResponse response = taskService.create(taskCreteRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(saveTask.getId());
        assertThat(response.title()).isEqualTo("Estudando TestContainer");
        assertThat(response.status()).isEqualTo(TaskStatus.PENDING);
        assertThat(response.priority()).isEqualTo(TaskPriority.MEDIUM);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve Lançar TaskNotFoundException ao Buscar por ID inexistente")
    void shouldThrowExceptionWhenTaskNotFound(){
        UUID randomId = UUID.randomUUID();
        when(taskRepository.findByIdAndUserId(randomId, mockUser.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(randomId))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining("Tarefa Não Encontrada para o ID fornecido: " + randomId);

        verify(taskRepository, times(1)).findByIdAndUserId(randomId, mockUser.getId());
    }

    @Test
    @DisplayName("Deve Alterar o status para COMPLETED ao concluir Tarefa")
    void shouldCompleteTaskSuccessfully(){
        UUID taskId = UUID.randomUUID();

        Task existingTask = Task.builder()
                .id(taskId)
                .title("Tarefa Pendente")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .user(mockUser)
                .build();

        when(taskRepository.findByIdAndUserId(taskId, mockUser.getId())).thenReturn(Optional.of(existingTask));

        TaskResponse response = taskService.completeTask(taskId);

        assertThat(response.status()).isEqualTo(TaskStatus.COMPLETED);
        verify(taskRepository, times(1)).findByIdAndUserId(taskId, mockUser.getId());
    }
}