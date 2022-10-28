package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.db.filter.repository.TransformRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransformServiceTest {

    TransformService transformService;
    ObjectMapper objectMapper;
    public MockWebServer mockBackEnd;
    ResponseEntity<Trade> expectedResponse;

    TransformRepository transformRepository;

    Trade body;
    @BeforeEach
    void setUp(){
        mockBackEnd = new MockWebServer();
        objectMapper = new ObjectMapper();
        transformRepository = new TransformRepository(mockBackEnd.url("/").url().toString());
        transformService = new TransformService(transformRepository);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        responseHeaders.set("content-length","294");
        body = new Trade();

        expectedResponse = new ResponseEntity(body,responseHeaders,HttpStatus.OK);

    }

    @Test
    void postFilteredData() throws JsonProcessingException {
        //GIVEN
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(expectedResponse))
        );

        //WHEN
        ResponseEntity<Trade> actual = transformService.postFilteredData(body);

        //THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse),objectMapper.writeValueAsString(actual));
    }
}