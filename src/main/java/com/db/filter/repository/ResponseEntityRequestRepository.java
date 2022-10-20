package com.db.filter.repository;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public interface ResponseEntityRequestRepository {
    public ResponseEntity<Exception> makePostRequest(String baseUrl,String uri, HashMap<String,Object> body);
}
