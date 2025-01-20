package com.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanSimulationRequest {

    private BigDecimal loanAmount;
    @JsonFormat(pattern = "yyyy-MM-dd") // Configura o padrão de data
    private LocalDate birthDate;
    private Integer paymentTermInMonths;
    private String interestRateType = "FIXED"; // Valor padrão será FIXED se não for enviado
    private double interestRateMargin = 0.0; // Valor padrão para a margem será 0.0
    private String currency = "BRL";

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getPaymentTermInMonths() {
        return paymentTermInMonths;
    }

    public void setPaymentTermInMonths(Integer paymentTermInMonths) {
        this.paymentTermInMonths = paymentTermInMonths;
    }

    public String getInterestRateType() {
        return this.interestRateType;
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    public double getInterestRateMargin() {
        return interestRateMargin;
    }

    public void setInterestRateMargin(double interestRateMargin) {
        this.interestRateMargin = interestRateMargin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}