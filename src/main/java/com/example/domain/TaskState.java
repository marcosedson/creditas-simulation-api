package com.example.domain;

import java.util.List;

public class TaskState {
    private String status;
    private List<LoanEntity> results;

    public TaskState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LoanEntity> getResults() {
        return results;
    }

    public void setResults(List<LoanEntity> results) {
        this.results = results;
    }
}