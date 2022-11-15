package com.db.filter.entity;

import lombok.Data;


import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChunkTrades {
    @NotNull
    Integer Id;
    Integer size;
    List<Trade> trades;
    Integer totalNumTrades;
}
