package com.db.filter.service;

import com.db.filter.entity.ExceptionLog;
import com.db.filter.repository.ExceptionsPostRequests;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ExceptionsService {

    ExceptionsPostRequests exceptionsPostRequests;

    public ExceptionsService(ExceptionsPostRequests exceptionsPostRequests){

        this.exceptionsPostRequests = exceptionsPostRequests;

    }



    public ResponseEntity<ExceptionLog> postException(String name, String type, String message, String trace, Date cobDate) {

        return exceptionsPostRequests.postException(name,type,message,trace,cobDate);

    }
}