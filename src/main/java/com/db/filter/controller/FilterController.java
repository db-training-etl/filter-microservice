package com.db.filter.controller;
import com.db.filter.entity.Trade;
import com.db.filter.service.FilterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FilterController {



    private final FilterService filterService;
    public FilterController(FilterService filterService) {
        super();
        this.filterService = filterService;


    }



    @PostMapping("/trades/filter")
    public ResponseEntity<Trade> postEnrichData(@RequestBody Trade enrichedData) {

        Trade trade = filterService.filterData(enrichedData);

        ResponseEntity<Trade> response;
        if(trade.equals(new Trade())){
            response = new ResponseEntity<>(trade,HttpStatus.BAD_REQUEST);
        }else{
            response = new ResponseEntity<>(trade,HttpStatus.CREATED);
        }

        return response;
    }


}
