package com.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@ApplicationScoped
public class CurrencyConversionService {


    private Client client = createClient();

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";

    @Produces
    @ApplicationScoped
    public Client createClient() {
        return ClientBuilder.newClient();
    }

    public double convert(String fromCurrency, String toCurrency, double amount) {
        Response response = client.target(API_URL + fromCurrency)
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() != 200) {
            throw new RuntimeException("Erro ao buscar taxas de câmbio. Status HTTP: " + response.getStatus());
        }

        Map<String, Object> ratesResponse = response.readEntity(Map.class);

        Map<String, Double> rates = (Map<String, Double>) ratesResponse.get("rates");

        Double conversionRate = rates.get(toCurrency);
        if (conversionRate == null) {
            throw new IllegalArgumentException("Moeda de destino inválida ou não suportada: " + toCurrency);
        }

        return amount * conversionRate;
    }
}