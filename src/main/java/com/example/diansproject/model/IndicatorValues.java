package com.example.diansproject.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class IndicatorValues{
    private ZonedDateTime timestamp;
    private double shortSMA;
    private double longSMA;
    private double rsi;
    private double macd;
}
