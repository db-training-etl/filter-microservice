package com.db.filter.service;

import com.db.filter.repository.ResponseEntityRequestRepository;
import com.db.filter.repository.ExceptionsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class ExceptionsService {

    //WebClient webClient;
    ResponseEntityRequestRepository responseEntityRequestRepository;
    String baseUrl;

    public ExceptionsService(){
        this.baseUrl = "http://localhost:8089/";//need to change
        responseEntityRequestRepository = new ExceptionsRepository();
        //webClient = WebClient.create(baseUrl);
    }

    public ExceptionsService(String baseUrl, ResponseEntityRequestRepository responseEntityRequestRepository) {
        this.baseUrl = baseUrl;
        this.responseEntityRequestRepository = responseEntityRequestRepository;
    }

    public ResponseEntity<Exception> postException(String name, String type, String message, String trace, Date cobDate) {
        HashMap<String,Object> requestBody = new HashMap<>();
        requestBody.put("name",name);
        requestBody.put("type",type);
        requestBody.put("message",message);
        requestBody.put("trace",trace);
        requestBody.put("cobDate",cobDate);

        return responseEntityRequestRepository.makePostRequest(baseUrl,"exceptions/save",requestBody);
    }
}
