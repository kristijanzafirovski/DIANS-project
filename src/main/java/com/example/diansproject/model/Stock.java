package com.example.diansproject.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Entity
@Table(name="stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "last_refreshed")
    private String lastRefreshed;

    @Column(name = "time_zone")
    private String timeZone;

    @ElementCollection
    @CollectionTable(name = "stock_time_series", joinColumns = @JoinColumn(name = "stock_id"))
    @MapKeyColumn(name = "date")
    private Map<LocalDate, DailyStockData> timeSeries;


    public Stock() {}


    public Stock(String symbol, String lastRefreshed, String timeZone, Map<LocalDate, DailyStockData> timeSeries) {
        this.symbol = symbol;
        this.lastRefreshed = lastRefreshed;
        this.timeZone = timeZone;
        this.timeSeries = timeSeries;
    }

}
