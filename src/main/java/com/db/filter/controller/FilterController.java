package com.db.filter.controller;
import com.db.filter.entity.ChunkTrades;
import com.db.filter.entity.Trade;
import com.db.filter.service.FilterOrquestrator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
public class FilterController {
    private final FilterOrquestrator filterOrquestrator;

    @PostMapping("/trades/filter")
    public ResponseEntity<Trade> postEnrichData(@RequestBody Trade enrichedData) {
        log.info("--------- ENTER POST TRADE FILTER /trades/filter ---------");
        Trade trade = filterOrquestrator.filterData(enrichedData);

        ResponseEntity<Trade> response;
        if(trade.equals(new Trade())){
            log.info("--------- BAD REQUEST /trades/filter ---------");
            response = new ResponseEntity<>(trade,HttpStatus.BAD_REQUEST);
        }else{
            log.info("--------- TRADE CREATED /trades/filter ---------");
            response = new ResponseEntity<>(trade,HttpStatus.CREATED);
        }
        log.info("--------- DONE TRADE /trades/filter ---------");
        return response;
    }

    @PostMapping("/trades/filter/list")
    public ResponseEntity<ChunkTrades> postFilterList(@RequestBody @Valid ChunkTrades enrichedData){
        log.info("--------- ENTER POST CHUNK /trades/filter/list ---------");
        ChunkTrades chunkTrades = filterOrquestrator.filterList(enrichedData);

        log.info("--------- DONE CHUNK /trades/filter/list ---------");
        return new ResponseEntity<>(chunkTrades,HttpStatus.OK);
    }

}
