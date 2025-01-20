package com.example.controller;

import com.example.domain.LoanEntity;
import com.example.domain.LoanSimulationRequest;
import com.example.response.ErrorResponse;
import com.example.response.StatusResponse;
import com.example.response.TaskResponse;
import com.example.service.LoanSimulationService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.util.ValidationUtils.validateLoanRequest;
import static com.example.util.ValidationUtils.validateLoanRequests;


@Path("/simulations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loan Simulations", description = "Endpoints de simulações de empréstimos.")
public class LoanSimulationController {

    @Inject
    LoanSimulationService simulationService; // CDI cuidará da instância

    @POST
    @Operation(summary = "Processa uma única simulação de empréstimo.")
    public Response processSingleSimulation(LoanSimulationRequest request) {
        try {
            validateLoanRequest(request);

            LoanEntity result = simulationService.processSimulation(request);

            return Response.ok(result).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao processar a simulação: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/tasks")
    @Operation(summary = "Inicia uma tarefa para processar múltiplas simulações.")
    public Response processSimulations(List<LoanSimulationRequest> requests) {
        try {
            validateLoanRequests(requests);
            String taskId = simulationService.startAsyncProcessing(requests);
            return Response.accepted(new TaskResponse(taskId)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao iniciar simulações: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/tasks/{taskId}/status")
    @Operation(summary = "Verifica o status de uma tarefa específica.")
    public CompletableFuture<Response> getSimulationTaskStatus(@PathParam("taskId") String taskId) {
        return simulationService.getTaskStatusAsync(taskId)
                .thenApply(status -> Response.ok(new StatusResponse(taskId, status)).build())
                .exceptionally(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Erro ao consultar status: " + e.getMessage()))
                        .build());
    }

    @GET
    @Path("/tasks/{taskId}/results")
    @Operation(summary = "Obtém os resultados de uma tarefa específica.")
    public CompletableFuture<Response> getSimulationTaskResults(@PathParam("taskId") String taskId) {
        return simulationService.getTaskResultsAsync(taskId)
                .thenApply(result -> Response.ok(result).build())
                .exceptionally(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ErrorResponse("Erro ao buscar resultados: " + e.getMessage()))
                        .build());
    }
}