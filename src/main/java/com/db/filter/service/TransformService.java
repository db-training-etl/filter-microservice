package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Data
public class TransformService {

    WebClient webClient;
    String baseUrl;

    ObjectMapper objectMapper;

    public TransformService() {
        this.baseUrl = "localhost:8085/";
        webClient = WebClient.create(baseUrl);
        objectMapper = new ObjectMapper();
    }

    public TransformService(String baseUrl) {
        this.baseUrl = baseUrl;
        webClient = WebClient.create(baseUrl);
        objectMapper = new ObjectMapper();
    }

    public List postFilteredData(List<Trade> filteredData) throws JsonProcessingException {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("configuration").build())
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(filteredData)))
                .retrieve()
                .bodyToMono(List.class)
                .block();
    }
}
