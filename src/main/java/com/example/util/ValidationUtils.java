package com.example.util;

import com.example.domain.LoanSimulationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ValidationUtils {

    public static void validateLoanRequests(List<LoanSimulationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("A lista de solicitações de simulação não pode estar vazia.");
        }

        for (LoanSimulationRequest request : requests) {
            validateLoanRequest(request);
        }
    }

    public static void validateLoanRequest(LoanSimulationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("A solicitação de simulação não pode ser nula.");
        }

        if (request.getLoanAmount() == null || request.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do empréstimo (loanAmount) deve ser maior que zero.");
        }

        if (request.getPaymentTermInMonths() == null || request.getPaymentTermInMonths() <= 0) {
            throw new IllegalArgumentException("O prazo (paymentTermInMonths) deve ser um número inteiro maior que zero.");
        }

        if (request.getBirthDate() == null) {
            throw new IllegalArgumentException("A data de nascimento (birthDate) é obrigatória.");
        }

        if (request.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento (birthDate) não pode ser no futuro.");
        }
    }
}