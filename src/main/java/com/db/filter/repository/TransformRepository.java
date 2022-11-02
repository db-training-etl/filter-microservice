package com.db.filter.repository;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Repository
public class TransformRepository implements TransformPostRequests{

    WebClient webClient;
    @Value("${urls.transformservice}")
    String baseUrl;

    ObjectMapper objectMapper;

    public TransformRepository() {
        //this.baseUrl = "localhost:8085/";
        webClient = WebClient.create(baseUrl);
        objectMapper = new ObjectMapper();
    }

    public TransformRepository(String baseUrl) {
        this.baseUrl = baseUrl;
        webClient = WebClient.create(baseUrl);
        objectMapper = new ObjectMapper();
    }

    @Override
    public ResponseEntity postFilteredData(Trade trade) throws JsonProcessingException {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("trades/save").build())
                .bodyValue(trade)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public ResponseEntity postFilteredList(List<Trade> trades) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("trades/list/save").build())
                .bodyValue(trades)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
