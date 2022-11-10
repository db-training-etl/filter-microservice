package com.db.filter.entity;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class Trade {
    @NotNull
    Integer id;
    String tradeName;
    @NotNull
    Integer bookId;
    String country;
    @NotNull
    Integer counterpartyId;
    String currency;
    Date cobDate;
    Double amount;
    Boolean tradeTax;
    Book book;
    Counterparty counterparty;
}