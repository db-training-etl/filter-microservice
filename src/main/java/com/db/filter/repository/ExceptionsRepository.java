package com.db.filter.repository;

import com.db.filter.entity.ExceptionLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.HashMap;
@Repository
public class ExceptionsRepository implements ExceptionsPostRequests {

    WebClient webClient;


    @Value("${urls.exceptionservice}")
    String baseUrl;

    public ExceptionsRepository(){
        webClient = WebClient.create(baseUrl);
    }

    public ExceptionsRepository(String baseUrl) {
        this.baseUrl = baseUrl;
        webClient = WebClient.create(baseUrl);
    }

    @Override
    public ResponseEntity postException(String name, String type, String message, String trace, Date cobDate) {
        HashMap<String,Object> requestBody = new HashMap<>();
        requestBody.put("name",name);
        requestBody.put("type",type);
        requestBody.put("message",message);
        requestBody.put("trace",trace);
        requestBody.put("cobDate",cobDate);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("exceptions").build())
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .toEntity(ExceptionLog.class)
                .block();
    }
}
