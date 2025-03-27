package com.example.diansproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "stocks")
public class Stock implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "last_refreshed")
    private LocalDate lastRefreshed;

    @Column(name = "time_zone")
    private String timeZone;

    @ElementCollection
    @CollectionTable(name = "stock_time_series", joinColumns = @JoinColumn(name = "stock_id"))
    @MapKeyColumn(name = "date")
    private Map<LocalDate, DailyStockData> timeSeries;

    @ElementCollection
    @CollectionTable(name = "stock_intraday_series", joinColumns = @JoinColumn(name = "stock_id"))
    private Map<String, IntradayStockData> intradayTimeSeries;

    @ElementCollection
    @CollectionTable(name = "hourly_intraday_series", joinColumns = @JoinColumn(name = "stock_id"))
    private Map<String, IntradayStockData> hourlyTimeSeries;

    @Transient
    private boolean isNew = true;

    public Stock() {}

    public Stock(String symbol, LocalDate lastRefreshed, String timeZone, Map<LocalDate, DailyStockData> timeSeries) {
        this.symbol = symbol;
        this.lastRefreshed = lastRefreshed;
        this.timeZone = timeZone;
        this.timeSeries = timeSeries != null ? new HashMap<>(timeSeries) : new HashMap<>();
        this.intradayTimeSeries = new HashMap<>();
    }

    public void setHourlyTimeSeries(Map<LocalDateTime, IntradayStockData> hourlyTS) {
        if (hourlyTS != null) {
            this.hourlyTimeSeries = new HashMap<>();
            hourlyTS.forEach((key, value) ->
                    this.hourlyTimeSeries.put(key.format(java.time.format.DateTimeFormatter.ISO_DATE_TIME), value)
            );
        } else {
            this.hourlyTimeSeries = new HashMap<>();
        }
    }

    public void setIntradayTimeSeries(Map<LocalDateTime, IntradayStockData> intradayTimeSeries) {
        if (intradayTimeSeries != null) {
            this.intradayTimeSeries = new HashMap<>();
            intradayTimeSeries.forEach((key, value) ->
                    this.intradayTimeSeries.put(key.format(java.time.format.DateTimeFormatter.ISO_DATE_TIME), value)
            );
        } else {
            this.intradayTimeSeries = new HashMap<>();
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
    @PrePersist
    void onCreate() {
        if (this.lastRefreshed == null) {
            this.lastRefreshed = LocalDate.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.lastRefreshed = LocalDate.now();
    }
}