package com.example.diansproject.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
public class StockAnalysis {
    private String intradaySignal;
    private String dailySignal;
    private String hourlySignal;

    public StockAnalysis(String intradaySignal, String dailySignal, String hourlySignal) {
        this.intradaySignal = intradaySignal;
        this.dailySignal = dailySignal;
        this.hourlySignal = hourlySignal;
    }


}

