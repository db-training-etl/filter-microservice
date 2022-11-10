package com.db.filter.ExceptionHandlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
public class CustomException extends RuntimeException{
    private String name;
    private String type;
    private String message;
    private String trace;
    private Date cobDate;

    public CustomException(String name, String type,String message,String trace,Date cobDate){
        this.cobDate=cobDate;
        this.name=name;
        this.message=message;
        this.type=type;
        this.trace=trace;
    }
}
