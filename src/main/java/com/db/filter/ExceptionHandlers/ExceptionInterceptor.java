package com.db.filter.ExceptionHandlers;

import com.db.filter.service.ExceptionsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Date;

@ControllerAdvice
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    ExceptionsService exceptionsService;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException exception){
        exceptionsService.postException(exception.getName(), exception.getType(), exception.getMessage(), exception.getTrace(), exception.getCobDate());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        exceptionsService.postException("Method Argument Not Valid", "Method Argument Not Valid", ex.getMessage(), ex.getStackTrace().toString(), Date.from(Instant.now()));
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
