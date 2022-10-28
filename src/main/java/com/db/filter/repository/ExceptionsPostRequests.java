package com.db.filter.repository;

import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface ExceptionsPostRequests {
    ResponseEntity postException(String name, String type, String message, String trace, Date cobDate);
}
