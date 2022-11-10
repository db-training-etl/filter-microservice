package com.db.filter.ExceptionHandlers;

import com.db.filter.service.ExceptionsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
}
