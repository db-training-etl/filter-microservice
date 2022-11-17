package com.db.filter.repository;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Repository
public class TransformRepository implements TransformPostRequests{

    WebClient webClient;
    @Value("${urls.transformservice}")
    String baseUrl;

    ObjectMapper objectMapper;

    public TransformRepository() {
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
        log.info("---------- SEND TRADE to TRANSFORM SERVICE ----------");
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("trades/save").build())
                .bodyValue(trade)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public ResponseEntity postFilteredList(List<Trade> trades) {
        log.info("---------- SEND TRADES to TRANSFORM SERVICE ----------");
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("trades/list/save").build())
                .bodyValue(trades)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
