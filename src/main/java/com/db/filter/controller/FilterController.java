package com.db.filter.controller;
import com.db.filter.entity.ChunckTrades;
import com.db.filter.entity.Trade;
import com.db.filter.service.FilterOrquestrator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@AllArgsConstructor
public class FilterController {



    private final FilterOrquestrator filterOrquestrator;




    @PostMapping("/trades/filter")
    public ResponseEntity<Trade> postEnrichData(@RequestBody Trade enrichedData) {

        Trade trade = filterOrquestrator.filterData(enrichedData);

        ResponseEntity<Trade> response;
        if(trade.equals(new Trade())){
            response = new ResponseEntity<>(trade,HttpStatus.BAD_REQUEST);
        }else{
            response = new ResponseEntity<>(trade,HttpStatus.CREATED);
        }

        return response;
    }

    @PostMapping("/trades/filter/list")
    public ResponseEntity<ChunckTrades> postFilterList(@RequestBody ChunckTrades enrichedData){

        ChunckTrades chunckTrades = filterOrquestrator.filterList(enrichedData);

        return new ResponseEntity<>(chunckTrades,HttpStatus.OK);
    }


}
