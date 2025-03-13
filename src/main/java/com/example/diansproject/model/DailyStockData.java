package com.example.diansproject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class DailyStockData {
    @Column(name = "open_price")
    private BigDecimal open;

    @Column(name = "high_price")
    private BigDecimal high;

    @Column(name = "low_price")
    private BigDecimal low;

    @Column(name = "close_price")
    private BigDecimal close;

    @Column(name = "volume")
    private Long volume;

    public DailyStockData() {
    }

    public DailyStockData(BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Long volume) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

}
