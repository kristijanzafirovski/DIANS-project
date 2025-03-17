package com.example.diansproject.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class IntradayStockData {
    private double open;
    private double high;
    private double low;
    private double volume;
    private double close;
}
