package com.db.filter.service;

import com.db.filter.entity.Trade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransformService {

    public List<Trade> postFilteredData(List<Trade> filteredData) {

        return new ArrayList<>();
    }
}
