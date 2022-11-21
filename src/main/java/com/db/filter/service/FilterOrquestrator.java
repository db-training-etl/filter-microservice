package com.db.filter.service;

import com.db.filter.ExceptionHandlers.CustomException;
import com.db.filter.entity.ChunkTrades;
import com.db.filter.entity.Trade;
import com.db.filter.repository.FileWriterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class FilterOrquestrator {

    private final TransformService transformService;

    private final FileWriterRepository fileWriterRepository;

    public Trade filterData(Trade trade){

        if(trade == null || trade.getId() == null){
            log.info("---------- TRADE HAS ID NULL ----------");
            return new Trade();
        }

        boolean isFiltered = checkIfTradeIsFiltered(trade);

        if(isFiltered){
            try {
                log.info("---------- SEND TRADE TO BE SAVED CSV ----------");
                fileWriterRepository.createFileWithFilteredData(trade);
            } catch (IOException e) {
                log.info("---------- IOEXCEPTION ----------");
                throw new CustomException("","Run Time Exception","","",Date.from(Instant.now()));
            }
        }

        sendDataToTransformService(trade);

        return trade;
    }

    public ChunkTrades filterList(ChunkTrades enrichedTrades) {

        List<Trade> filtered = enrichedTrades.getTrades().stream()
                                 .filter(trade -> checkIfTradeIsFiltered(trade))
                                 .collect(Collectors.toList());

        List<Trade> nonFiltered = enrichedTrades.getTrades().stream()
                                    .filter(trade -> !checkIfTradeIsFiltered(trade))
                                    .collect(Collectors.toList());

        filtered.stream().forEach(trade -> {
            try {
                log.info("---------- SEND FILTERED TRADES TO BE SAVED INTO CSV ----------");
                fileWriterRepository.createFileWithFilteredData(trade);
            } catch (IOException e) {
                log.info("---------- IOEXCEPTION ----------");
                throw new CustomException("","Rune Time Exception","","",Date.from(Instant.now()));
            }
        });

        transformService.postFilteredList(nonFiltered);

        ChunkTrades nonFilteredTrades = new ChunkTrades();
        nonFilteredTrades.setId(enrichedTrades.getId());
        nonFilteredTrades.setTrades(nonFiltered);
        nonFilteredTrades.setSize(enrichedTrades.getSize());
        nonFilteredTrades.setTotalNumTrades(enrichedTrades.getTotalNumTrades());
        return nonFilteredTrades;
    }

    private boolean checkIfTradeIsFiltered(Trade trade) {
        return trade.getAmount() <= 0 || "JPN".equals(trade.getCurrency());
    }

    private void sendDataToTransformService(Trade nonFilteredTrades) {
        try {
            log.info("---------- SEND TRADE TO TRANSFORM SERVICE ----------");
            transformService.postFilteredData(nonFilteredTrades);
        } catch (JsonProcessingException e) {
            log.info("---------- JsonProcessingException ----------");
            throw new CustomException("","Rune Time Exception","","",Date.from(Instant.now()));
        }
    }

}
