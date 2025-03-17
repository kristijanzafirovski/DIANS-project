package com.example.diansproject.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StockAnalysis {
    // Getters and setters
    private List<String> intradaySignals;
    private List<String> dailySignals;

    public StockAnalysis() {}

    public StockAnalysis(List<String> intradaySignals, List<String> dailySignals) {
        this.intradaySignals = intradaySignals;
        this.dailySignals = dailySignals;
    }

}
