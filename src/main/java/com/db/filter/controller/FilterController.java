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



    private final FilterService filterService;
    public FilterController(FilterService filterService) {
        super();
        this.filterService = filterService;


    }



    @PostMapping("/trades/filter")
    public ResponseEntity<Trade> postEnrichData(@RequestBody Trade enrichedData) {

        if(enrichedData.equals(new Trade()) || enrichedData == null){
            return new ResponseEntity<>(new Trade(),HttpStatus.BAD_REQUEST);
        }

        Trade trade = filterService.filterData(enrichedData);

        ResponseEntity<Trade> response = new ResponseEntity<>(enrichedData, HttpStatus.CREATED);

        return response;
    }


}
