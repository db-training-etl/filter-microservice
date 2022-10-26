package com.db.filter.service;

import com.db.filter.entity.ExceptionLog;
import com.db.filter.repository.PostRequests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.HashMap;

@Service
public class ExceptionsService {

    WebClient webClient;



    String baseUrl;

    public ExceptionsService(){
        this.baseUrl = "http://localhost:8089/";//need to change
        webClient = WebClient.create(baseUrl);
    }

    public ExceptionsService(String baseUrl) {
        this.baseUrl = baseUrl;
        webClient = WebClient.create(baseUrl);
    }

    public ResponseEntity<ExceptionLog> postException(String name, String type, String message, String trace, Date cobDate) {
        HashMap<String,Object> requestBody = new HashMap<>();
        requestBody.put("name",name);
        requestBody.put("type",type);
        requestBody.put("message",message);
        requestBody.put("trace",trace);
        requestBody.put("cobDate",cobDate);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("exceptions/save").build())
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .toEntity(ExceptionLog.class)
                .block();
    }
}
