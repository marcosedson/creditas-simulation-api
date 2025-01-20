package com.example.service;

import com.example.domain.LoanEntity;
import com.example.domain.LoanSimulationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanSimulationServiceImplTest {

    @Mock
    CurrencyConversionService currencyConversionService;

    @InjectMocks
    LoanSimulationServiceImpl loanSimulationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessSimulation_LocalCurrency() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        LoanEntity result = loanSimulationService.processSimulation(request);

        assertNotNull(result);
        assertTrue(result.totalInterest().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testProcessSimulation_ConvertedCurrency() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("USD");

        when(currencyConversionService.convert("USD", "BRL", 10000.0)).thenReturn(50000.0);

        LoanEntity result = loanSimulationService.processSimulation(request);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(50000.0), request.getLoanAmount());
        verify(currencyConversionService, times(1)).convert("USD", "BRL", 10000.0);
    }

    @Test
    void testStartAsyncProcessing() throws ExecutionException, InterruptedException {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        String taskId = loanSimulationService.startAsyncProcessing(List.of(request));

        assertNotNull(taskId);
        assertEquals("COMPLETED", loanSimulationService.getTaskStatusAsync(taskId).get());
    }
}