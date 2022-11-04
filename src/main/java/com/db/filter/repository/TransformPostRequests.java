package com.db.filter.repository;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransformPostRequests {
    ResponseEntity postFilteredData(Trade trade) throws JsonProcessingException;
    ResponseEntity postFilteredList(List<Trade> trades);
}
