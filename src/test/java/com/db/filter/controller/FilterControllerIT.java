package com.db.filter.controller;

import com.db.filter.entity.Book;
import com.db.filter.entity.Counterparty;
import com.db.filter.entity.Trade;
import com.db.filter.service.ExceptionsService;
import com.db.filter.service.TransformService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public class FilterControllerIT {

    @MockBean
    TransformService transformService;

    @MockBean
    ExceptionsService exceptionsService;

    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    List<Trade> inventedTrades;
    Counterparty counterparty;
    Book book;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        inventedTrades = new ArrayList<>();
        counterparty = new Counterparty();
        counterparty.setCounterpartyId(0);
        counterparty.setEntity("richman");
        counterparty.setCounterpartyName("aaaaa");
        counterparty.setSource("aaaa");

        book = new Book();
        book.setBookId(0);
        book.setEntity("entity test");
        book.setBookName("Santander");
        book.setBookAddress("hola");

        Trade trade = new Trade();
        trade.setId(0);
        trade.setTradeName("test1");
        trade.setBookId(book.getBookId());
        trade.setCountry("spain");
        trade.setCounterpartyId(counterparty.getCounterpartyId());
        trade.setCurrency("USD");
        trade.setCobDate(Date.from(Instant.now()));
        trade.setAmount(100.0);
        trade.setTradeTax(true);
        trade.setBook(book);
        trade.setCounterparty(counterparty);


        inventedTrades.add(trade);

        trade = new Trade();
        trade.setId(1);
        trade.setTradeName("test2");
        trade.setBookId(book.getBookId());
        trade.setCountry("aaaaa");
        trade.setCounterpartyId(counterparty.getCounterpartyId());
        trade.setCurrency("USD");
        trade.setCobDate(Date.from(Instant.now()));
        trade.setAmount(0.0);
        trade.setTradeTax(true);
        trade.setBook(book);
        trade.setCounterparty(counterparty);

        inventedTrades.add(trade);

        trade = new Trade();
        trade.setId(2);
        trade.setTradeName("test2");
        trade.setBookId(book.getBookId());
        trade.setCountry("aaaaa");
        trade.setCounterpartyId(counterparty.getCounterpartyId());
        trade.setCurrency("JPN");
        trade.setCobDate(Date.from(Instant.now()));
        trade.setAmount(10.0);
        trade.setTradeTax(true);
        trade.setBook(book);
        trade.setCounterparty(counterparty);

        inventedTrades.add(trade);

        trade = new Trade();
        trade.setId(3);
        trade.setTradeName("test3");
        trade.setBookId(book.getBookId());
        trade.setCountry("aaaaa");
        trade.setCounterpartyId(counterparty.getCounterpartyId());
        trade.setCurrency("USD");
        trade.setCobDate(Date.from(Instant.now()));

        trade.setAmount(10.0);
        trade.setTradeTax(true);
        trade.setBook(book);
        trade.setCounterparty(counterparty);

        inventedTrades.add(trade);


    }

    @Test
    void postEnrichedData() throws Exception {
        //GIVEN
        ResponseEntity<List<Trade>> result = new ResponseEntity<>(inventedTrades,HttpStatus.CREATED);

        given(transformService.postFilteredData(inventedTrades)).willReturn(inventedTrades);
        //WHEN
        ResultActions response = mockMvc.perform(post("/Trades/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventedTrades)));
        //THEN
        response.andExpect(status().isCreated()).andDo(print());
    }

    @Test
    void postEnrichedDataNullBodyThrowException() throws Exception {
        //GIVEN
        ResponseEntity result = new ResponseEntity<>(new ArrayList<>(), HttpStatus.CREATED);

        given(transformService.postFilteredData(null)).willThrow(new RuntimeException());
        given(exceptionsService.postException(any(),any(),any(),any(),any())).willReturn(result);

        //WHEN
        ResultActions response = mockMvc.perform(post("/Trades/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)));
        //THEN
        response.andExpect(status().isBadRequest()).andDo(print());
    }
    
}
