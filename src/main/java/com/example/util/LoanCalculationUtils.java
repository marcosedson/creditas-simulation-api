package com.example.util;

import com.example.domain.LoanEntity;
import com.example.domain.LoanSimulationRequest;
import com.example.enums.Taxes;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@ApplicationScoped
public class LoanCalculationUtils {

    public static LoanEntity calculateLoan(LoanSimulationRequest request) {
        int age = calculateAge(request.getBirthDate());
        double monthlyRate = getMonthlyRate(request, age);

        double loanAmount = request.getLoanAmount().doubleValue();

        int n = request.getPaymentTermInMonths();

        double monthlyPayment = loanAmount * monthlyRate / (1 - Math.pow(1 + monthlyRate, -n));
        double totalAmount = monthlyPayment * n;
        double totalInterest = totalAmount - loanAmount;

        return new LoanEntity(
                BigDecimal.valueOf(round(totalAmount)),
                BigDecimal.valueOf(round(monthlyPayment)),
                BigDecimal.valueOf(round(totalInterest))
        );
    }

    private static double getMonthlyRate(LoanSimulationRequest request, int age) {
        double baseAnnualRate = getAnnualRate(age);

        double annualRate;
        if (Taxes.VARIABLE.name().equalsIgnoreCase(request.getInterestRateType())) {
            annualRate = baseAnnualRate + request.getInterestRateMargin();
        } else if (Taxes.FIXED.name().equalsIgnoreCase(request.getInterestRateType())) {
            annualRate = baseAnnualRate;
        } else {
            throw new IllegalArgumentException("Tipo de taxa de juros inválido. Use 'FIXED' ou 'VARIABLE'.");
        }

        return annualRate / 12;
    }

    static int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private static double getAnnualRate(int age) {
        if (age <= 25) {
            return 0.05;
        } else if (age <= 40) {
            return 0.03;
        } else if (age <= 60) {
            return 0.02;
        } else {
            return 0.04;
        }
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}