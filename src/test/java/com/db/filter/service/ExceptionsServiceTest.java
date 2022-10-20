package com.db.filter.service;

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
    ObjectMapper objectMapper;
    public MockWebServer mockBackEnd;
    ResponseEntity<Exception> expectedResponse;

    ExceptionsRepository exceptionsRepository;

    @BeforeEach
    void setUp(){
        exceptionsRepository = new ExceptionsRepository();
        mockBackEnd = new MockWebServer();
        objectMapper = new ObjectMapper();
        exceptionsService = new ExceptionsService(mockBackEnd.url("/").url().toString(), exceptionsRepository);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        responseHeaders.set("content-length","22204");

        expectedResponse = new ResponseEntity<Exception>(new Exception(),responseHeaders,200);


    }

    @Test
    void postException() throws JsonProcessingException {
        //GIVEN
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(expectedResponse))
        );

        //WHEN
        ResponseEntity<Exception> actual = exceptionsService.postException("name","type","message","trace", Date.from(Instant.now()));

        //THEN
        assertEquals(expectedResponse.toString(),actual.toString());
    }
}