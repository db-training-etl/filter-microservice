package com.db.filter.entity;

import lombok.Data;

import java.util.List;

@Data
public class ChunckTrades {
    int Id;
    List<Trade> trades;
    int totalNumTrades;
}
