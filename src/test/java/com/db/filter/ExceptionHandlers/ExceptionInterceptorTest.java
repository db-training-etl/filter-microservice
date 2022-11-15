package com.db.filter.ExceptionHandlers;

import com.db.filter.controller.FilterController;
import com.db.filter.entity.ChunkTrades;
import com.db.filter.service.ExceptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExceptionInterceptorTest {

    ExceptionInterceptor exception;

    @Mock
    ExceptionsService exceptionsService;

    @Mock
    FilterController filterController;

    MockMvc mockMvc;

    ObjectMapper mapper;

    @BeforeEach
    void setUp(){
        exception = new ExceptionInterceptor(exceptionsService);
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(filterController).setControllerAdvice(new ExceptionInterceptor(exceptionsService)).build();
    }

    @Test
    void GIVEN_CustomException_WHEN_ExceptionIsThrown_THEN_ExceptionInterceptorCatchEceptionAndCallExceptionService(){
        CustomException customException = new CustomException("name","type","message","trace", Date.from(Instant.now()));

        ResponseEntity actual = exception.handleCustomException(customException);
        ResponseEntity expected = new ResponseEntity<>(customException,HttpStatus.BAD_REQUEST);

        verify(exceptionsService,times(1)).postException(any(),any(),any(),any(),any());

        assertEquals(expected,actual);
    }

    @Test
    void GIVEN_MethodArgumentNotValid_WHEN_ExceptionIsThrown_THEN_ExceptionInterceptorCatchEceptionAndCallExceptionService() throws Exception {

        ChunkTrades chunck = new ChunkTrades();

        mockMvc.perform(post("/trades/filter/list")
                .content(mapper.writeValueAsString(chunck))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
        verify(exceptionsService,times(1)).postException(any(),any(),any(),any(),any());
    }

}