package com.wdenberg.task.controller;

import com.wdenberg.task.domain.model.TaskStatus;
import com.wdenberg.task.dto.TaskCreteRequest;
import com.wdenberg.task.dto.TaskResponse;
import com.wdenberg.task.dto.TaskUpdateRequest;
import com.wdenberg.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Gerenciamento de Tarefas", description = "Endpoints para Criar, consultar Tarefas")
public class TaskController {

    private final TaskService taskService;


    @Operation(summary = "Listar todas as Tarefas ", description = "Retorna uma Lista com Todas as tarefas")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTask(){
        return ResponseEntity.ok(taskService.finAll());
    }

    @Operation(summary = "Buscar uma tarefa pelo seu ID", description = "Retorna os Detalhes de uma Task Especifica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa Encontrada com Sucesso"),
            @ApiResponse(responseCode = "400", description = "Tarefa não Encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "UUID da tarefa", required = true)
        @PathVariable UUID id
            ){
        return ResponseEntity.ok(taskService.findById(id));
    }



    @Operation(summary = "Filtra tarefas por Status", description = "Retorna tarefa filtrando por PENDING, IN_PROGRESS, COMPLETED ou CANCELLED.")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTaskByStatus(
            @Parameter(description = "Status da Tarefa")
            @PathVariable TaskStatus status
            ){
        return ResponseEntity.ok(taskService.findByStatus(status));
    }

    @Operation(summary = "Criar nova tarefa", description = "Cria um novo registro de tarefa no banco de dados com status inicial PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados")
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreteRequest taskCreteRequest
            ){
        TaskResponse createdTask = taskService.create(taskCreteRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.id())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }


    @Operation(summary = "Atualizar tarefa", description = "Atualiza os dados de uma tarefa existente (título, descrição, status ou data limite).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest){
        return ResponseEntity.ok(taskService.update(id, taskUpdateRequest));
    }

    @Operation(summary = "Excluir tarefa", description = "Remove permanentemente uma tarefa baseando-se em seu UUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso (Sem conteúdo de retorno)"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable UUID id){
        taskService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(summary = "Concluir tarefa rapidamente", description = "Altera o status de uma tarefa para COMPLETED com uma única requisição PATCH.")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable UUID id){
        return ResponseEntity.ok(taskService.completeTask(id));
    }
}
