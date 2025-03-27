package com.example.diansproject.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class StockAnalysis {

    private String symbol;
    private String latestDailySignal;
    private String latestIntradaySignal;
    private String latestHourlySignal;
    private Map<LocalDate, DailyStockData> dailyData;
    private Map<LocalDateTime, IntradayStockData> intradayData;

    // Getters and setters...
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLatestDailySignal() {
        return latestDailySignal;
    }

    public void setLatestDailySignal(String latestDailySignal) {
        this.latestDailySignal = latestDailySignal;
    }

    public String getLatestIntradaySignal() {
        return latestIntradaySignal;
    }

    public void setLatestIntradaySignal(String latestIntradaySignal) {
        this.latestIntradaySignal = latestIntradaySignal;
    }

    public Map<LocalDate, DailyStockData> getDailyData() {
        return dailyData;
    }

    public void setDailyData(Map<LocalDate, DailyStockData> dailyData) {
        this.dailyData = dailyData;
    }

    public Map<LocalDateTime, IntradayStockData> getIntradayData() {
        return intradayData;
    }

    public void setIntradayData(Map<LocalDateTime, IntradayStockData> intradayData) {
        this.intradayData = intradayData;
    }

    public void setLatestHourlySignal(String hourlySignal) {
        this.latestHourlySignal = hourlySignal;
    }

    public String getLatestHourlySignal() {
        return latestHourlySignal;
    }
}