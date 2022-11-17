package com.db.filter.ExceptionHandlers;

import com.db.filter.service.ExceptionsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Instant;
import java.util.Date;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    ExceptionsService exceptionsService;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException exception){
        log.info("---------- CATCH CustomException ----------" + exception.toString());
        exceptionsService.postException(exception.getName(), exception.getType(), exception.getMessage(), exception.getTrace(), exception.getCobDate());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info("---------- CATCH ResponseEntityExceptionHandler ----------" + ex.toString());
        exceptionsService.postException("Method Argument Not Valid", "Method Argument Not Valid", ex.getMessage(), ExceptionUtils.getStackTrace(ex), Date.from(Instant.now()));
        return super.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, request);
    }
}
