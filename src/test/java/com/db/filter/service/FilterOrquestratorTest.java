package com.db.filter.service;

import com.db.filter.ExceptionHandlers.CustomException;
import com.db.filter.entity.*;
import com.db.filter.repository.FileWriterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


class FilterOrquestratorTest {

    FilterOrquestrator filterOrquestrator;


    TransformService transformService;


    FileWriterRepository fileWriterRepository;


    List<Trade> inventedTrades;
    Counterparty counterparty;
    Book book;

    @BeforeEach
    void setUp(){
        transformService = mock(TransformService.class);
        fileWriterRepository = mock(FileWriterRepository.class);
        filterOrquestrator = new FilterOrquestrator(transformService,fileWriterRepository);

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
    void noFilterData() throws JsonProcessingException {
        ResponseEntity<Trade> result = new ResponseEntity<>(HttpStatus.CREATED);
        given(transformService.postFilteredData(filterData(inventedTrades.get(0)))).willReturn(result);

        Trade actual = filterOrquestrator.filterData(inventedTrades.get(0));
        Trade expected = filterData(inventedTrades.get(0));

        assertEquals(expected,actual);
    }

    @Test
    void filterData() throws JsonProcessingException {
        ResponseEntity<Trade> result = new ResponseEntity<>(HttpStatus.CREATED);
        given(transformService.postFilteredData(filterData(inventedTrades.get(1)))).willReturn(result);

        Trade actual = filterOrquestrator.filterData(inventedTrades.get(1));
        Trade expected = filterData(inventedTrades.get(1));

        assertEquals(expected,actual);
    }
    @Test
    void GIVEN_Trade_WHEN_FileHasWrongName_THEN_ThrowException() throws IOException {
        ResponseEntity<Trade> result = new ResponseEntity<>(HttpStatus.CREATED);
        Trade data = new Trade();
        data.setId(1);
        data.setAmount(0.0);

        given(transformService.postFilteredData(filterData(any()))).willReturn(result);
        doThrow(new IOException()).doNothing().when(fileWriterRepository).createFileWithFilteredData(data);



        assertThrows(RuntimeException.class,()->filterOrquestrator.filterData(data));
    }

    @Test
    void filterDataSendEmptyBody() throws JsonProcessingException {
        ResponseEntity<Trade> result = new ResponseEntity<>(HttpStatus.CREATED);

        ResponseEntity<ExceptionLog> expectedResultFromService = new ResponseEntity<>(HttpStatus.ACCEPTED);
        given(transformService.postFilteredData(new Trade())).willReturn(result);

        Trade actual = filterOrquestrator.filterData(new Trade());
        Trade expected = filterData(new Trade());

        assertEquals(expected,actual);

    }

    @Test
    void GIVEN_Trade_WHEN_SendToTransformServiceAndJSONProcessingFail_THEN_ThrowRuntimeException() throws JsonProcessingException {
        given(transformService.postFilteredData(any())).willThrow(new JsonProcessingException("Error"){});

        assertThrows(RuntimeException.class,()->filterOrquestrator.filterData(inventedTrades.get(0)));
    }

    @Test
    void GIVEN_ListOfTrades_WHEN_AllOk_THEN_ReturnNoFilteredTrades() throws IOException {

        ChunkTrades chunkTrades = new ChunkTrades();
        chunkTrades.setTrades(inventedTrades);

        ChunkTrades actual = filterOrquestrator.filterList(chunkTrades);
        ChunkTrades expected = filterList(inventedTrades);

        verify(fileWriterRepository,times(inventedTrades.size()- actual.getTrades().size())).createFileWithFilteredData(any());


        assertEquals(expected.toString(),actual.toString());
    }

    @Test
    void GIVEN_ListOfTrades_WHEN_CobDateMissing_THEN_ThrowRunTimeException() throws IOException {
        List<Trade> tradeCobDateMissing = new ArrayList<>();
        Trade trade = new Trade();
        trade.setId(0);
        trade.setAmount(0.0);
        tradeCobDateMissing.add(trade);

        ChunkTrades chunkTrades = new ChunkTrades();
        chunkTrades.setTrades(tradeCobDateMissing);

        doThrow(new IOException()).doNothing().when(fileWriterRepository).createFileWithFilteredData(any());

        assertThrows(CustomException.class, () -> filterOrquestrator.filterList(chunkTrades));

        verify(fileWriterRepository,times(tradeCobDateMissing.size())).createFileWithFilteredData(any());

    }


    private ChunkTrades filterList(List<Trade> data){
        List<Trade> nonFiltered = new ArrayList<>();

        for (Trade trade: data) {
            if(trade.getAmount() > 0 && !"JPN".equals(trade.getCurrency())){
                nonFiltered.add(trade);
            }
        }

        ChunkTrades chunck = new ChunkTrades();
        chunck.setTrades(nonFiltered);

        return chunck;
    }

    private Trade filterData(Trade data) {

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd");


        if (data==null || data.equals(new Trade())) {
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