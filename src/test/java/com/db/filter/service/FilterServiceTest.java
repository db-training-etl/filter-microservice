package com.db.filter.service;

import com.db.filter.entity.Book;
import com.db.filter.entity.Counterparty;
import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FilterServiceTest {

    FilterService filterService;


    TransformService transformService;


    ExceptionsService exceptionsService;
    List<Trade> inventedTrades;
    Counterparty counterparty;
    Book book;

    @BeforeEach
    void setUp(){
        transformService = mock(TransformService.class);
        exceptionsService = mock(ExceptionsService.class);
        filterService = new FilterService(transformService,exceptionsService);

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
    void filterData() throws JsonProcessingException {

        given(transformService.postFilteredData(filterData(inventedTrades))).willReturn(filterData(inventedTrades));

        List<Trade> actual = filterService.filterData(inventedTrades);
        List<Trade> expected = filterData(inventedTrades);

        assertEquals(expected,actual);
    }

    @Test
    void filterDataSendEmptyBody() throws JsonProcessingException {

        ResponseEntity<Exception> expectedResultFromService = new ResponseEntity<>(HttpStatus.ACCEPTED);
        given(transformService.postFilteredData(new ArrayList<>())).willReturn(new ArrayList<>());
        given(exceptionsService.postException("","","","",Date.from(Instant.now()))).willReturn(expectedResultFromService);

        List<Trade> actual = filterService.filterData(new ArrayList<>());
        List<Trade> expected = filterData(new ArrayList<>());

        assertEquals(expected,actual);

    }

    @Test
    void filterDataThrowException() throws JsonProcessingException {
        List<Trade> expectedResultFromService = new ArrayList<>();
        given(transformService.postFilteredData(expectedResultFromService)).willReturn(expectedResultFromService);

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

        assertThrows(RuntimeException.class,()->filterService.filterData(inventedTrades));
    }

    @Test
    void filterDataThrowJsonProcessingException() throws JsonProcessingException {
        given(transformService.postFilteredData(any())).willThrow(new JsonProcessingException("Error"){});

        assertThrows(RuntimeException.class,()->filterService.filterData(inventedTrades));
    }

    private List<Trade> filterData(List<Trade> data) {

        TimeZone utc = TimeZone.getTimeZone("UTC");

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        HashMap<String, Object> filteredData = new HashMap<>();

        List<Trade> filteredTrades = new ArrayList<>();
        List<Trade> nonFilteredTrades = new ArrayList<>();

        if (data.isEmpty()) {
            return new ArrayList<>();
        }


        for (Trade trade : data) {

            if (trade.getAmount() > 0 && !"JPN".equals(trade.getCurrency())) {
                nonFilteredTrades.add(trade);
            } else {
                filteredTrades.add(trade);
            }
        }

        try {
            if (!filteredTrades.isEmpty()) {
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