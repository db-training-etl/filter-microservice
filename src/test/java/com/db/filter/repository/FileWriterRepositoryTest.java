package com.db.filter.repository;

import com.db.filter.entity.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterRepositoryTest {

    FileWriterRepository fileWriterRepository;
    String FILE_PATH = "./src/main/resources/filtered-tradeName-";

    @BeforeEach
    void setUp(){
        fileWriterRepository = new FileWriterRepository(FILE_PATH);
    }

    @Test
    void GIVEN_Trade_WHEN_dataOk_THEN_AppendDataToFile() throws IOException {
        Trade data = new Trade();
        data.setId(1);
        data.setAmount(0.0);
        data.setCobDate(Date.from(Instant.now()));

        fileWriterRepository.createFileWithFilteredData(data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        File csvFile = new File(fileWriterRepository.FILE_PATH + dateFormat.format(data.getCobDate()) + ".csv");

        assertTrue(csvFile.exists());
    }

}