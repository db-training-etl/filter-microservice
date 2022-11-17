package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.db.filter.repository.TransformPostRequests;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransformService {
    TransformPostRequests transformPostRequests;

    public TransformService(TransformPostRequests transformPostRequests) {
        this.transformPostRequests = transformPostRequests;

    }

    public ResponseEntity postFilteredData(Trade trade) throws JsonProcessingException {
        log.info("---------- SEND TRADE TO TRANSFORM SERVICE ----------");
        return transformPostRequests.postFilteredData(trade);
    }

    public ResponseEntity postFilteredList(List<Trade> trades){
        log.info("---------- SEND LIST OF TRADE TO TRANSFORM SERVICE ----------");
        return transformPostRequests.postFilteredList(trades);
    }
}
