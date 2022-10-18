package com.db.filter.controller;

import com.db.filter.entity.Book;
import com.db.filter.entity.Counterparty;
import com.db.filter.entity.Trade;
import com.db.filter.service.ExceptionsService;
import com.db.filter.service.TransformService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class FilterControllerTest {

    FilterController filterController;


    TransformService trasformService;
    ExceptionsService exceptionsService;

    List<Trade> inventedTrades;
    Counterparty counterparty;
    Book book;

    @BeforeEach
    void setUp(){
        trasformService = mock(TransformService.class);
        exceptionsService = mock(ExceptionsService.class);
        filterController = new FilterController(trasformService,exceptionsService);



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
    void postEnrichData(){
        List<Trade> expectedResultFromService = new ArrayList<>();
        given(trasformService.postFilteredData(expectedResultFromService)).willReturn(expectedResultFromService);

        ResponseEntity<List<Trade>> enrichDataResponse = filterController.postEnrichData(inventedTrades);
        ResponseEntity<List<Trade>> expected = new ResponseEntity<List<Trade>>(filterData(inventedTrades), HttpStatus.CREATED);

        assertEquals(expected,enrichDataResponse);
    }

    @Test
    void postEnrichDataThrowExceptionNoRequestedBody(){
        List<Trade> emptyResponse = new ArrayList<>();
        List<Trade> expectedResultFromService = new ArrayList<>();
        given(trasformService.postFilteredData(expectedResultFromService)).willReturn(expectedResultFromService);
        given(exceptionsService.postException()).willReturn(expectedResultFromService);

        ResponseEntity<List<Trade>> enrichDataResponse = filterController.postEnrichData(emptyResponse);
        ResponseEntity<List<Trade>> expected = new ResponseEntity<List<Trade>>(filterData(emptyResponse), HttpStatus.BAD_REQUEST);
        assertEquals(expected,enrichDataResponse);
    }

    @Test
    void postEnrichDataThrowIOExcpetion() {
        List<Trade> expectedResultFromService = new ArrayList<>();
        given(trasformService.postFilteredData(expectedResultFromService)).willReturn(expectedResultFromService);

        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inventedTrades= new ArrayList<>();

        Trade trade = new Trade();
        trade.setId(3);
        trade.setTradeName("test3");
        trade.setBookId(book.getBookId());
        trade.setCountry("aaaaa");
        trade.setCounterpartyId(counterparty.getCounterpartyId());
        trade.setCurrency("USD");
        trade.setCobDate(null);
        trade.setAmount(0.0);
        trade.setTradeTax(true);
        trade.setBook(book);
        trade.setCounterparty(counterparty);

        inventedTrades.add(trade);

        assertThrows(RuntimeException.class,()->filterController.postEnrichData(inventedTrades));

    }

    private List<Trade> filterData(List<Trade> data){

        TimeZone utc = TimeZone.getTimeZone("UTC");

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        HashMap<String,Object> filteredData = new HashMap<>();

        List<Trade> filteredTrades = new ArrayList<>();
        List<Trade> nonFilteredTrades = new ArrayList<>();

        if(data.isEmpty()){
            return new ArrayList<>();
        }



        for (Trade trade: data) {

            if(trade.getAmount()>0 && trade.getCurrency()!="JPN"){
                nonFilteredTrades.add(trade);
            }else{
                filteredTrades.add(trade);
            }
        }

        try {
            if(!filteredTrades.isEmpty()) {
                FileWriter writer = new FileWriter("./src/test/resources/filtered-flowtype-" + destFormat.format(filteredTrades.get(0).getCobDate()).replace(":", "-") + ".csv");
                ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy<>();
                mappingStrategy.setType(Trade.class);

                String[] columns = {"id", "tradeName", "bookId", "country", "counterpartyId", "currency", "cobDate", "amount", "tradeTax", "book", "counterparty"};
                mappingStrategy.setColumnMapping(columns);


                StatefulBeanToCsvBuilder<Trade> builder = new StatefulBeanToCsvBuilder(writer);
                StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();


                beanWriter.write(filteredTrades);


                writer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return nonFilteredTrades;
    }
}