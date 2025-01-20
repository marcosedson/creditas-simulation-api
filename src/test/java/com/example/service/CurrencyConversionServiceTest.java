package com.example.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyConversionServiceTest {

    @Mock
    private Client mockClient; // Mock do Cliente HTTP

    @Mock
    private WebTarget mockWebTarget;

    @Mock
    private Invocation.Builder mockBuilder;

    @Mock
    private Response mockResponse;

    private CurrencyConversionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CurrencyConversionService() {
            public Client createClient() {
                return mockClient;
            }
        };
    }

    @Test
    void testConvertWithValidResponse() {
        String fromCurrency = "USD";
        String toCurrency = "BRL";
        double amount = 100.0;

        Map<String, Object> ratesResponse = new HashMap<>();
        Map<String, Double> rates = new HashMap<>();
        rates.put("BRL", 5.0);
        ratesResponse.put("rates", rates);

        when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Map.class)).thenReturn(ratesResponse);

        double result = service.convert(fromCurrency, toCurrency, amount);

        assertEquals(500.0, result);
        verify(mockClient).target("https://api.exchangerate-api.com/v4/latest/USD");
    }

    @Test
    void testConvertWithInvalidCurrency() {
        String fromCurrency = "USD";
        String toCurrency = "INVALID";
        double amount = 100.0;

        Map<String, Object> ratesResponse = new HashMap<>();
        ratesResponse.put("rates", new HashMap<>());

        when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Map.class)).thenReturn(ratesResponse);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                service.convert(fromCurrency, toCurrency, amount)
        );

        assertEquals("Moeda de destino inválida ou não suportada: INVALID", exception.getMessage());
    }

    @Test
    void testConvertWithErrorResponse() {
        String fromCurrency = "USD";
        String toCurrency = "BRL";
        double amount = 100.0;

        when(mockClient.target(Mockito.anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request(Mockito.anyString())).thenReturn(mockBuilder);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(500);

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.convert(fromCurrency, toCurrency, amount)
        );

        assertTrue(exception.getMessage().contains("Erro ao buscar taxas de câmbio"));
    }
}