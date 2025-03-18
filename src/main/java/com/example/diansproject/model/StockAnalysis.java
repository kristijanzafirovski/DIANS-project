package com.example.diansproject.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Setter
@Getter
public class StockAnalysis {
    private List<IndicatorValues> intradayValues;
    private List<IndicatorValues> dailyValues;
    private List<String> intradaySignals;
    private List<String> dailySignals;

    public StockAnalysis(List<IndicatorValues> intradayValues,
                         List<IndicatorValues> dailyValues,
                         List<String> intradaySignals,
                         List<String> dailySignals) {
        this.intradayValues = intradayValues;
        this.dailyValues = dailyValues;
        this.intradaySignals = intradaySignals;
        this.dailySignals = dailySignals;
    }
}

