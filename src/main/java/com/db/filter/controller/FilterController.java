package com.db.filter.controller;
import com.db.filter.entity.ChunkTrades;
import com.db.filter.entity.Trade;
import com.db.filter.service.FilterOrquestrator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


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
    public ResponseEntity<ChunkTrades> postFilterList(@RequestBody @Valid ChunkTrades enrichedData){

        ChunkTrades chunkTrades = filterOrquestrator.filterList(enrichedData);

        return new ResponseEntity<>(chunkTrades,HttpStatus.OK);
    }

}
