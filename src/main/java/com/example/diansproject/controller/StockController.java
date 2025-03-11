package com.example.diansproject.controller;

import com.example.diansproject.service.ingest.impl.StockDataService;
import com.tecacet.finance.model.Quote;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockDataService stockDataService;

    public StockController(StockDataService stockDataService) {
        this.stockDataService = stockDataService;
    }

    @GetMapping("/{symbol}/history")
    public ResponseEntity<?> getStockHistory(
            @PathVariable String symbol,
            @RequestParam String from,
            @RequestParam String to) {

        try {
            LocalDate startDate = LocalDate.parse(from);
            LocalDate endDate = LocalDate.parse(to);

            List<Quote> history = stockDataService.getHistoricalPrices(
                    symbol,
                    startDate,
                    endDate
            );

            return ResponseEntity.ok(history);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve stock data"));
        }
    }

    @GetMapping("/{symbol}/dividends")
    public ResponseEntity<?> getDividendHistory(
            @PathVariable String symbol,
            @RequestParam String from,
            @RequestParam String to) {

        try {
            LocalDate startDate = LocalDate.parse(from);
            LocalDate endDate = LocalDate.parse(to);

            Map<LocalDate, BigDecimal> dividends = stockDataService.getDividends(
                    symbol,
                    startDate,
                    endDate
            );

            return ResponseEntity.ok(dividends);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve dividend data"));
        }
    }
}
