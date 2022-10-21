package com.db.filter.controller;
import com.db.filter.entity.Trade;
import com.db.filter.service.ExceptionsService;
import com.db.filter.service.TransformService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@RestController
public class FilterController {

    List<Trade> ddbbEnrichedData;

    private final TransformService transformService;
    private final ExceptionsService exceptionsService;

    public FilterController(TransformService transformService,ExceptionsService exceptionsService) {
        super();
        this.transformService = transformService;
        this.exceptionsService = exceptionsService;
        ddbbEnrichedData = new ArrayList<>();
    }



    @PostMapping("/Trades/filter")
    public ResponseEntity<List<Trade>> postEnrichData(@RequestBody List<Trade> enrichedData) {
        ddbbEnrichedData = new ArrayList<>();
        //prepare date formatter
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Trade> filteredTrades = new ArrayList<>();
        List<Trade> nonFilteredTrades = new ArrayList<>();

        if(enrichedData.isEmpty()){
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }

        for (Trade trade: enrichedData) {

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
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }

        ddbbEnrichedData.addAll(nonFilteredTrades);

        ResponseEntity<List<Trade>> response = new ResponseEntity<>(ddbbEnrichedData, HttpStatus.CREATED);

        //Call next service to store data in a XML
        try {
            transformService.postFilteredData(ddbbEnrichedData);
        } catch (JsonProcessingException e) {
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }

        return response;
    }

    private void sendException(String name,String type, String message,String trace, Date cobDate) {
        exceptionsService.postException(name,type,message,trace,cobDate);
    }

}
