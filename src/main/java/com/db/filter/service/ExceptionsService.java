package com.db.filter.service;

import com.db.filter.entity.ExceptionLog;
import com.db.filter.repository.PostRequests;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.HashMap;

@Service
public class ExceptionsService {

    PostRequests postRequests;

    public ExceptionsService(PostRequests postRequests){

        this.postRequests = postRequests;

    }



    public ResponseEntity<ExceptionLog> postException(String name, String type, String message, String trace, Date cobDate) {

        return postRequests.postException(name,type,message,trace,cobDate);

    }
}