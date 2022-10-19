package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
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
    List expectedResponse;

    @BeforeEach
    void setUp(){
        mockBackEnd = new MockWebServer();
        objectMapper = new ObjectMapper();
        transformService = new TransformService(mockBackEnd.url("/").url().toString());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        responseHeaders.set("content-length","22204");

        expectedResponse = new ArrayList<>();
        Trade trade = new Trade();
        expectedResponse.add(trade);
    }

    @Test
    void postFilteredData() throws JsonProcessingException {
        List<Trade> filteredData = new ArrayList<>();
        Trade trade = new Trade();
        filteredData.add(trade);

        //GIVEN
        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(expectedResponse))
        );

        //WHEN
        List actual = transformService.postFilteredData(filteredData);

        //THEN
        assertEquals(objectMapper.writeValueAsString(expectedResponse),objectMapper.writeValueAsString(actual));
    }
}