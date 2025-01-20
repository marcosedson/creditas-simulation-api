package com.example.service;

import com.example.domain.LoanEntity;
import com.example.domain.LoanSimulationRequest;
import com.example.util.LoanCalculationUtils;
import com.example.domain.TaskState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@ApplicationScoped
public class LoanSimulationServiceImpl implements LoanSimulationService {

    @Inject
    CurrencyConversionService currencyConversionService;

    @Inject
    ObjectMapper objectMapper;

    private final ConcurrentMap<String, TaskState> taskStates = new ConcurrentHashMap<>();

    private final ThreadPoolExecutor executorService;

    @Inject
    public LoanSimulationServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.executorService = new ThreadPoolExecutor(
                10,
                50,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public LoanEntity processSimulation(LoanSimulationRequest request) {
        try {
            var BRL = "BRL";
            if (!BRL.equalsIgnoreCase(request.getCurrency())) {
                double amountInBRL = currencyConversionService.convert(
                        request.getCurrency(), BRL, request.getLoanAmount().doubleValue()
                );
                request.setLoanAmount(BigDecimal.valueOf(amountInBRL));
                request.setCurrency(BRL);
            }
            return LoanCalculationUtils.calculateLoan(request);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar simulação única", e);
        }
    }

    @Override
    public String startAsyncProcessing(List<LoanSimulationRequest> requests) {
        String taskId = UUID.randomUUID().toString();
        TaskState taskState = new TaskState("PROCESSING");
        taskStates.put(taskId, taskState);

        executorService.submit(() -> {
            try {
                List<LoanEntity> results = new ArrayList<>();
                for (LoanSimulationRequest request : requests) {
                    LoanEntity simulationResult = processSimulation(request);
                    results.add(simulationResult);
                }

                taskState.setResults(results);
                taskState.setStatus("COMPLETED");
                System.out.println("Tarefa concluída com sucesso. Task ID: " + taskId);

            } catch (Exception e) {
                taskState.setStatus("FAILED");
                System.err.println("Erro ao processar tarefa. Task ID: " + taskId + " Erro: " + e.getMessage());
            }
        });

        return taskId;
    }

    @Override
    public CompletableFuture<String> getTaskStatusAsync(String taskId) {
        return CompletableFuture.supplyAsync(() -> {
            TaskState state = taskStates.get(taskId);
            if (state == null) throw new IllegalArgumentException("Task ID não encontrado.");
            return state.getStatus();
        });
    }

    @Override
    public CompletableFuture<List<LoanEntity>> getTaskResultsAsync(String taskId) {
        return CompletableFuture.supplyAsync(() -> {
            TaskState state = taskStates.get(taskId);
            if (state == null) throw new IllegalArgumentException("Task ID não encontrado.");
            if (!"COMPLETED".equals(state.getStatus()))
                throw new IllegalStateException("Resultados ainda não disponíveis.");
            return state.getResults();
        });
    }
}