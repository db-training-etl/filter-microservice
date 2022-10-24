package com.db.filter.controller;
import com.db.filter.entity.Trade;
import com.db.filter.service.ExceptionsService;
import com.db.filter.service.FilterService;
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

    private final FilterService filterService;
    public FilterController(FilterService filterService) {
        super();
        this.filterService = filterService;

        ddbbEnrichedData = new ArrayList<>();
    }



    @PostMapping("/Trades/filter")
    public ResponseEntity<List<Trade>> postEnrichData(@RequestBody List<Trade> enrichedData) {

        ddbbEnrichedData = filterService.filterData(enrichedData);

        if(ddbbEnrichedData.isEmpty()){
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<List<Trade>> response = new ResponseEntity<>(ddbbEnrichedData, HttpStatus.CREATED);



        return response;
    }


}
