package com.db.filter.ExceptionHandlers;

import com.db.filter.repository.FileWriterRepository;
import com.db.filter.service.ExceptionsService;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionInterceptorTest {

    ExceptionInterceptor exception;

    @Mock
    ExceptionsService exceptionsService;

    @BeforeEach
    void setUp(){

        exception = new ExceptionInterceptor(exceptionsService);
    }

    @Test
    void GIVEN_CustomException_WHEN_ExceptionIsThrown_THEN_ExceptionInterceptorCatchEceptionAndCallExceptionService(){
        CustomException customException = new CustomException("name","type","message","trace", Date.from(Instant.now()));

        ResponseEntity actual = exception.handleCustomException(customException);
        ResponseEntity expected = new ResponseEntity<>(customException,HttpStatus.BAD_REQUEST);

        assertEquals(expected,actual);
    }

}