package com.wdenberg.task.controller;

import com.wdenberg.task.domain.model.TaskStatus;
import com.wdenberg.task.dto.TaskCreteRequest;
import com.wdenberg.task.dto.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine");


    @LocalServerPort
    private int port;


    private RestClient restClient;


    @BeforeEach
    void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }


    @Test
    @DisplayName("Deve realizar ciclo completo CRUD na API REST utilizando PostgreSQL real no Docker")
    void shouldPerformCrudOperationsSuccessfully() {

        // Criar tarefa
        TaskCreteRequest createRequest = new TaskCreteRequest(
                "Integrar com Docker",
                "Subir aplicação e banco em containers via Docker Compose",
                LocalDateTime.now().plusDays(5)
        );


        TaskResponse createdTask = restClient.post()
                .uri("/api/v1/tasks")
                .body(createRequest)
                .retrieve()
                .body(TaskResponse.class);


        assertThat(createdTask).isNotNull();
        assertThat(createdTask.title())
                .isEqualTo("Integrar com Docker");
        assertThat(createdTask.status())
                .isEqualTo(TaskStatus.PENDING);


        var taskId = createdTask.id();

        assertThat(taskId).isNotNull();



        // Buscar tarefa criada
        TaskResponse foundTask = restClient.get()
                .uri("/api/v1/tasks/{id}", taskId)
                .retrieve()
                .body(TaskResponse.class);


        assertThat(foundTask).isNotNull();
        assertThat(foundTask.id())
                .isEqualTo(taskId);



        // Completar tarefa
        TaskResponse completedTask = restClient.patch()
                .uri("/api/v1/tasks/{id}/complete", taskId)
                .retrieve()
                .body(TaskResponse.class);


        assertThat(completedTask).isNotNull();
        assertThat(completedTask.status())
                .isEqualTo(TaskStatus.COMPLETED);



        // Deletar tarefa
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/tasks/{id}", taskId)
                .retrieve()
                .toBodilessEntity();


        assertThat(deleteResponse.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);



        // Validar que não existe mais
        ResponseEntity<Void> notFoundResponse = restClient.get()
                .uri("/api/v1/tasks/{id}", taskId)
                .exchange((request, response) ->
                        ResponseEntity.status(response.getStatusCode())
                                .build()
                );

        assertThat(notFoundResponse.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}