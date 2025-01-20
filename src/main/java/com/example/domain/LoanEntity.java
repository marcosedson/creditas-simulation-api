package com.example.domain;

import java.math.BigDecimal;

public record LoanEntity(BigDecimal totalAmount, BigDecimal monthlyPayment, BigDecimal totalInterest) {}