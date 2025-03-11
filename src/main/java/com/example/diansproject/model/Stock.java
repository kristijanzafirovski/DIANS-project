package com.example.diansproject.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCKS")
@Data
@AllArgsConstructor
@Builder
public class Stock {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private LocalDateTime timestamp;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;

    protected Stock() {} // Required by JPA

    public Stock(String symbol, LocalDateTime timestamp, Double open, Double high,
                 Double low, Double close, Long volume) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public void setId(Long id) {
        this.id = id;
    }

}