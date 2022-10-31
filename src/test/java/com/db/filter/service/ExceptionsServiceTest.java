package com.db.filter.service;

import com.db.filter.entity.ExceptionLog;
import com.db.filter.repository.ExceptionsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsServiceTest {

    ExceptionsService exceptionsService;
    ExceptionsRepository webclientRepository;
    ObjectMapper objectMapper;
    public MockWebServer mockBackEnd;
    ResponseEntity<ExceptionLog> expectedResponse;

    @BeforeEach
    void setUp(){
        mockBackEnd = new MockWebServer();
        objectMapper = new ObjectMapper();
        webclientRepository = new ExceptionsRepository(mockBackEnd.url("/").url().toString());
        exceptionsService = new ExceptionsService(webclientRepository);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        responseHeaders.set("content-length","200");

        expectedResponse = new ResponseEntity<ExceptionLog>(new ExceptionLog(),responseHeaders,200);


    }

    @Test
    void postException() throws JsonProcessingException {
        //GIVEN
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(expectedResponse))
        );

        //WHEN
        ResponseEntity<ExceptionLog> actual = exceptionsService.postException("name","type","message","trace", Date.from(Instant.now()));

        //THEN
        assertEquals(expectedResponse.toString(),actual.toString());
    }
}