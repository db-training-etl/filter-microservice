package com.db.filter.repository;

import com.db.filter.entity.Trade;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

@Slf4j
@NoArgsConstructor
@Repository
@AllArgsConstructor
public class FileWriterRepository {

    @Value("${filepath.src}")
    String FILE_PATH;


    public void createFileWithFilteredData(Trade data) throws IOException {
        log.info("---------- SAVE TRADE into CSV ----------");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        File csvFile = new File(FILE_PATH + dateFormat.format(data.getCobDate()) + ".csv");
        if(!csvFile.exists()) {
            csvFile.createNewFile();
            addHeaderToNewFile(csvFile);
        }

        FileWriter writer = new FileWriter(csvFile,true);
        writeData(data, writer);

        writer.close();
    }

    private void writeData(Trade data, FileWriter writer) throws IOException {
        writer.write(data.getId().toString()+';'+ data.getTradeName()+';'+ data.getBookId()+';'+ data.getCountry()+';'+ data.getCounterpartyId()+';'+
                data.getCurrency()+';'+ data.getCobDate()+';'+ data.getAmount()+';'+ data.getTradeTax()+';'+ data.getBook()+';'+ data.getCounterparty()+'\n');
    }

    private void addHeaderToNewFile(File csvFile) throws IOException {
        FileWriter writerHeader = new FileWriter(csvFile);
        String[] columns = {"id", "tradeName", "bookId", "country", "counterpartyId", "currency",
                "cobDate", "amount", "tradeTax", "book", "counterparty"};

        StringBuilder line = new StringBuilder();

        String header = Stream.of(columns).reduce("",(finalString,element) -> finalString + element + ";");

        line.append(header);
        line.append('\n');

        writerHeader.write(line.toString());

        writerHeader.close();
    }
}
