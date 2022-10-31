package com.db.filter.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ExceptionLog {
    Integer id;
    String name;
    String type;
    String message;
    String trace;
    Date cobDate;
}
