package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class FilterService {

    private final TransformService transformService;
    private final ExceptionsService exceptionsService;

    public List<Trade> filterData(List<Trade> enrichedData){

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Trade> filteredTrades = new ArrayList<>();
        List<Trade> nonFilteredTrades = new ArrayList<>();

        if(enrichedData.isEmpty()){
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            return new ArrayList<>();
        }

        for (Trade trade: enrichedData) {
            if(trade.getAmount()>0 && !"JPN".equals(trade.getCurrency())){
                nonFilteredTrades.add(trade);
            }else{
                filteredTrades.add(trade);
            }
        }

        createFileWithFilteredData(destFormat, filteredTrades);

        sendDataToTransformService(nonFilteredTrades);

        return nonFilteredTrades;
    }

    private void createFileWithFilteredData(SimpleDateFormat destFormat, List<Trade> filteredTrades) {
        try {
            if(!filteredTrades.isEmpty()) {
                FileWriter writer = new FileWriter("./src/main/resources/filtered-flowtype-"
                        + destFormat.format(filteredTrades.get(0).getCobDate()).replace(":", "-") + ".csv");
                ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy<>();
                mappingStrategy.setType(Trade.class);
                String[] columns = {"id", "tradeName", "bookId", "country", "counterpartyId", "currency",
                        "cobDate", "amount", "tradeTax", "book", "counterparty"};
                mappingStrategy.setColumnMapping(columns);

                StatefulBeanToCsvBuilder<Trade> builder = new StatefulBeanToCsvBuilder(writer);
                StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();


                beanWriter.write(filteredTrades);
                writer.close();
            }
        } catch (Exception e) {
            //throw exception and call exception service and send him the log

            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }
    }

    private void sendDataToTransformService(List<Trade> nonFilteredTrades) {
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
}
