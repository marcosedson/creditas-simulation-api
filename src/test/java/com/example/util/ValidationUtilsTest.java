package com.example.util;

import com.example.domain.LoanSimulationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void testValidateLoanRequestValid() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> ValidationUtils.validateLoanRequest(request));
    }

    @Test
    void testValidateLoanRequestNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ValidationUtils.validateLoanRequest(null)
        );

        assertEquals("A solicitação de simulação não pode ser nula.", exception.getMessage());
    }

    @Test
    void testValidateLoanRequestInvalidAmount() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(-1));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ValidationUtils.validateLoanRequest(request)
        );

        assertEquals("O valor do empréstimo (loanAmount) deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void testValidateLoanRequestInvalidDate() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(1000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.now().plusDays(1));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ValidationUtils.validateLoanRequest(request)
        );

        assertEquals("A data de nascimento (birthDate) não pode ser no futuro.", exception.getMessage());
    }

    @Test
    void testValidateLoanRequestsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ValidationUtils.validateLoanRequests(Collections.emptyList())
        );

        assertEquals("A lista de solicitações de simulação não pode estar vazia.", exception.getMessage());
    }

    @Test
    void testValidateLoanRequestsValidList() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> ValidationUtils.validateLoanRequests(List.of(request)));
    }
}