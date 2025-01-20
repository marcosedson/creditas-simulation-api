package com.example.service;

import com.example.domain.LoanEntity;
import com.example.domain.LoanSimulationRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LoanSimulationService {
    LoanEntity processSimulation(LoanSimulationRequest request);
    String startAsyncProcessing(List<LoanSimulationRequest> requests);
    CompletableFuture<String> getTaskStatusAsync(String taskId);
    CompletableFuture<List<LoanEntity>> getTaskResultsAsync(String taskId);
}