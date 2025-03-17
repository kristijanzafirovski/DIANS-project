package com.example.diansproject.model;

import java.util.List;

public class StockAnalysis {
    private List<String> intradaySignals;
    private List<String> dailySignals;

    public StockAnalysis() {}

    public StockAnalysis(List<String> intradaySignals, List<String> dailySignals) {
        this.intradaySignals = intradaySignals;
        this.dailySignals = dailySignals;
    }

    // Getters and setters
    public List<String> getIntradaySignals() { return intradaySignals; }
    public void setIntradaySignals(List<String> intradaySignals) { this.intradaySignals = intradaySignals; }
    public List<String> getDailySignals() { return dailySignals; }
    public void setDailySignals(List<String> dailySignals) { this.dailySignals = dailySignals; }
}
