package com.db.filter.controller;

import com.db.filter.entity.Trade;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@RestController
public class FilterController {

    HashMap<String,Object> ddbbEnrichedData;

    public FilterController() {
        ddbbEnrichedData = new HashMap<>();
    }

    @PostMapping("/v1/SaveEnrichedData")
    public ResponseEntity<HashMap<String,Object>> postEnrichData(@RequestBody HashMap<String,Object> enrichedData) {


        //prepare date formatter
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Trade> filteredTrades = new ArrayList<>();
        List<Trade> nonFilteredTrades = new ArrayList<>();

        if(enrichedData.isEmpty()){

            return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
        }

        List<Trade> trades = (List<Trade>) enrichedData.get("trades");

        for (Trade trade: trades) {

            if(trade.getAmount()>0 && trade.getCurrency()!="JPN"){
                nonFilteredTrades.add(trade);
            }else{
                filteredTrades.add(trade);
            }
        }

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

            throw new RuntimeException(e);
        }

        ddbbEnrichedData.put("trades",nonFilteredTrades);

        ResponseEntity<HashMap<String,Object>> response = new ResponseEntity<>(ddbbEnrichedData, HttpStatus.CREATED);

        return response;
    }
}
