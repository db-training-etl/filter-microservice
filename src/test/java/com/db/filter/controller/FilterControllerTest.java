package com.db.filter.controller;

import com.db.filter.entity.Book;
import com.db.filter.entity.Counterparty;
import com.db.filter.entity.Trade;
import com.db.filter.service.FilterOrquestrator;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


class FilterControllerTest {

    FilterController filterController;

    FilterOrquestrator filterService;

    List<Trade> inventedTrades;
    Counterparty counterparty;
    Book book;


    @BeforeEach
    void setUp() {
        filterService = mock(FilterOrquestrator.class);
        filterController = new FilterController(filterService);


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
    void postEnrichData() {
        given(filterService.filterData(inventedTrades.get(0))).willReturn(filterData(inventedTrades.get(0)));

        ResponseEntity<Trade> enrichDataResponse = filterController.postEnrichData(inventedTrades.get(0));
        ResponseEntity<Trade> expected = new ResponseEntity<Trade>(filterData(inventedTrades.get(0)), HttpStatus.CREATED);

        assertEquals(expected, enrichDataResponse);
    }

    @Test
    void postEnrichDataThrowExceptionNoRequestedBody() {
        Trade emptyResponse = new Trade();

        given(filterService.filterData(emptyResponse)).willReturn(emptyResponse);

        ResponseEntity<Trade> enrichDataResponse = filterController.postEnrichData(emptyResponse);
        ResponseEntity<Trade> expected = new ResponseEntity<Trade>(filterData(emptyResponse), HttpStatus.BAD_REQUEST);
        assertEquals(expected, enrichDataResponse);
    }

    private Trade filterData(Trade data) {

        TimeZone utc = TimeZone.getTimeZone("UTC");

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (data.equals(new Trade()) || data==null) {
            return new Trade();
        }
        boolean isFiltered;



        if (data.getAmount() > 0 && !"JPN".equals(data.getCurrency())) {
            isFiltered=false;
        } else {
            isFiltered=true;
        }


        try {
            if (isFiltered) {
                FileWriter writer = new FileWriter("./src/test/resources/filtered-flowtype-" + destFormat.format(data.getCobDate()).replace(":", "-") + ".csv");
                ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy<>();
                mappingStrategy.setType(Trade.class);

                String[] columns = {"id", "tradeName", "bookId", "country", "counterpartyId", "currency", "cobDate", "amount", "tradeTax", "book", "counterparty"};
                mappingStrategy.setColumnMapping(columns);


                StatefulBeanToCsvBuilder<Trade> builder = new StatefulBeanToCsvBuilder(writer);
                StatefulBeanToCsv beanWriter = builder.withMappingStrategy(mappingStrategy).build();


                beanWriter.write(data);


                writer.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return data;
    }
}