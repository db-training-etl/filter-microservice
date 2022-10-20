package com.db.filter.repository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;

public class ExceptionsRepository implements ResponseEntityRequestRepository {

    public ExceptionsRepository() {

    }

    @Override
    public ResponseEntity<Exception> makePostRequest(String baseUrl,String uri, HashMap<String,Object> body) {

        return WebClient.create(baseUrl).post()
                .uri(uriBuilder -> uriBuilder.path(uri).build())
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .toEntity(Exception.class)
                .block();
    }
}
