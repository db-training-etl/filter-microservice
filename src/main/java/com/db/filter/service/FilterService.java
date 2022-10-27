package com.db.filter.service;

import com.db.filter.entity.Trade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class FilterService {

    private final TransformService transformService;
    private final ExceptionsService exceptionsService;

    private final String FILE_PATH = "./src/main/resources/filtered-tradeName-";

    public Trade filterData(Trade trade){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if(trade == null || trade.equals(new Trade())){
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            return new Trade();
        }

        boolean isFiltered = trade.getAmount() <= 0 || "JPN".equals(trade.getCurrency());

        createFileWithFilteredData(dateFormat, isFiltered, trade);

        sendDataToTransformService(trade);

        return trade;
    }

    private void createFileWithFilteredData(SimpleDateFormat destFormat, boolean isFiltered, Trade data) {
        try {
            if(isFiltered) {
                File file = new File(FILE_PATH
                        + destFormat.format(data.getCobDate()).replace(":", "-") + ".csv");

                createFileIfNotExist(destFormat, data, file);

                List list = readExistingLinesFromFile(destFormat, data);

                CSVWriter writer = setUpCsvwriter(destFormat, data);

                Iterator it = list.iterator();
                while(it.hasNext()) {
                    writer.writeNext((String[]) it.next());
                }

                writer.writeNext(convertTradeToStringArray(data));

                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private CSVWriter setUpCsvwriter(SimpleDateFormat destFormat, Trade data) throws IOException {
        return new CSVWriter(new FileWriter(FILE_PATH
                + destFormat.format(data.getCobDate()).replace(":", "-") + ".csv"), ';',
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
    }

    @NotNull
    private String[] convertTradeToStringArray(Trade data) {
        return new String[]{data.getId().toString(), data.getTradeName(), data.getBookId().toString()
                , data.getCountry(), data.getCounterpartyId().toString(), data.getCurrency(), data.getCobDate().toString()
                , data.getAmount().toString(), data.getTradeTax().toString(), data.getBook().toString(), data.getCounterparty().toString()};
    }

    private List readExistingLinesFromFile(SimpleDateFormat destFormat, Trade data) throws IOException, CsvException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader reader = new CSVReaderBuilder(
                new FileReader(FILE_PATH
                + destFormat.format(data.getCobDate()).replace(":", "-") + ".csv"))
                .withCSVParser(parser).build();

        List list = reader.readAll();

        reader.close();
        return list;
    }

    private void createFileIfNotExist(SimpleDateFormat destFormat, Trade data, File file) throws IOException {
        if(!file.exists()){
            file.createNewFile();

            CSVWriter writerHeader = setUpCsvwriter(destFormat, data);
            String[] columns = {"id", "tradeName", "bookId", "country", "counterpartyId", "currency",
                    "cobDate", "amount", "tradeTax", "book", "counterparty"};

            writerHeader.writeNext(columns);
            writerHeader.flush();
            writerHeader.close();

        }
    }

    private void sendDataToTransformService(Trade nonFilteredTrades) {
        try {
            transformService.postFilteredData(nonFilteredTrades);
        } catch (JsonProcessingException e) {
            sendException("","Runtime Exception","","",Date.from(Instant.now()));
            throw new RuntimeException(e);
        }
    }

    private void sendException(String name,String type, String message,String trace, Date cobDate) {
        exceptionsService.postException(name,type,message,trace,cobDate);
    }
}
