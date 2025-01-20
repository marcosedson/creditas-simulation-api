package com.example.controller;

import com.example.domain.LoanSimulationRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class LoanSimulationControllerTest {

    @Test
    void testProcessSingleSimulationEndpoint() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/simulations")
                .then()
                .statusCode(200)
                .body("monthlyPayment", notNullValue());
    }

    @Test
    void testProcessSimulationTasksEndpoint() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        given()
                .contentType("application/json")
                .body(List.of(request))
                .when()
                .post("/simulations/tasks")
                .then()
                .statusCode(202)
                .body("taskId", notNullValue());
    }

    @Test
    void testGetSimulationTaskStatus() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        String taskId = given()
                .contentType("application/json")
                .body(List.of(request))
                .when()
                .post("/simulations/tasks")
                .then()
                .statusCode(202)
                .extract()
                .path("taskId");

        given()
                .contentType("application/json")
                .when()
                .get("/simulations/tasks/" + taskId + "/status")
                .then()
                .statusCode(200)
                .body("taskId", is(taskId))
                .body("status", notNullValue());
    }

    @Test
    void testGetSimulationTaskResults() {
        LoanSimulationRequest request = new LoanSimulationRequest();
        request.setLoanAmount(BigDecimal.valueOf(10000));
        request.setPaymentTermInMonths(12);
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setCurrency("BRL");

        String taskId = given()
                .contentType("application/json")
                .body(List.of(request))
                .when()
                .post("/simulations/tasks")
                .then()
                .statusCode(202)
                .extract()
                .path("taskId");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        given()
                .contentType("application/json")
                .when()
                .get("/simulations/tasks/" + taskId + "/results")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }
}