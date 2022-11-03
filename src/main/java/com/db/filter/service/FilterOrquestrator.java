package com.db.filter.service;

import com.db.filter.entity.ChunckTrades;
import com.db.filter.entity.Trade;
import com.db.filter.repository.FileWriterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class FilterOrquestrator {

    private final TransformService transformService;
    private final ExceptionsService exceptionsService;

    private final FileWriterRepository fileWriterRepository;


    public FilterOrquestrator(TransformService transformService, ExceptionsService exceptionsService, FileWriterRepository fileWriterRepository) {
        this.transformService = transformService;
        this.exceptionsService = exceptionsService;
        this.fileWriterRepository = fileWriterRepository;
    }

    public Trade filterData(Trade trade){

        if(trade == null || trade.getId() == null){
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            return new Trade();
        }

        boolean isFiltered = trade.getAmount() <= 0 || "JPN".equals(trade.getCurrency());

        if(isFiltered){
            try {
                fileWriterRepository.createFileWithFilteredData(trade);
            } catch (IOException e) {
                sendException("","Rune Time Exception","","",Date.from(Instant.now()));
                throw new RuntimeException(e);
            }
        }

        sendDataToTransformService(trade);

        return trade;
    }

    private void sendDataToTransformService(Trade nonFilteredTrades) {
        try {
            transformService.postFilteredData(nonFilteredTrades);
        } catch (JsonProcessingException e) {
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }
    }

    private void sendException(String name,String type, String message,String trace, Date cobDate) {
        exceptionsService.postException(name,type,message,trace,cobDate);
    }

    public List<Trade> filterList(ChunckTrades enrichedTrades) {

        List<Trade> nonFiltered = new ArrayList<>();
        List<Trade> filtered = new ArrayList<>();

        for (Trade trade: enrichedTrades.getTrades()) {
            if(trade.getAmount() <= 0 || "JPN".equals(trade.getCurrency())){
                filtered.add(trade);
            }else{
                nonFiltered.add(trade);
            }
        }

        for (Trade trade: filtered) {
            try {
                fileWriterRepository.createFileWithFilteredData(trade);
            } catch (IOException e) {
                sendException("","Rune Time Exception","","",Date.from(Instant.now()));
                throw new RuntimeException(e);
            }
        }

        //falta afegir la funcio d'enviar llista no filtrada al service de transform

        transformService.postFilteredList(nonFiltered);

        return nonFiltered;
    }
}
