package com.db.filter.entity;
import lombok.*;
import java.util.Date;

@Data
public class Trade {
    Integer id;
    String tradeName;
    Integer bookId;
    String country;
    Integer counterpartyId;
    String currency;
    Date cobDate;
    Double amount;
    Boolean tradeTax;
    Book book;
    Counterparty counterparty;
}