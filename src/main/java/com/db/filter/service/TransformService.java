package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.db.filter.repository.TransformPostRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TransformService {
    TransformPostRequests transformPostRequests;

    public TransformService(TransformPostRequests transformPostRequests) {
        this.transformPostRequests = transformPostRequests;

    }

    public ResponseEntity postFilteredData(Trade trade) throws JsonProcessingException {

        return transformPostRequests.postFilteredData(trade);
    }

    public ResponseEntity postFilteredList(List<Trade> trades){

        return transformPostRequests.postFilteredList(trades);
    }
}
