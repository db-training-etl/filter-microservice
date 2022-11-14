package com.db.filter.entity;

import lombok.Data;


import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChunckTrades {
    @NotNull
    Integer Id;
    List<Trade> trades;
    int totalNumTrades;
}
