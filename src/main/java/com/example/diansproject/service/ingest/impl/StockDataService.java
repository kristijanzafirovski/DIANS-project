package com.example.diansproject.service.ingest.impl;

import com.tecacet.finance.model.Quote;
import com.tecacet.finance.model.StandardPeriodType;
import com.tecacet.finance.service.stock.DividendService;
import com.tecacet.finance.service.stock.StockPriceService;
import jakarta.persistence.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class StockDataService {

    private final StockPriceService stockPriceService;
    private final DividendService dividendService;


    public StockDataService(StockPriceService stockPriceService,
                            DividendService dividendService) {
        this.stockPriceService = stockPriceService;
        this.dividendService = dividendService;
    }

    public List<Quote> getHistoricalPrices(String symbol,
                                           LocalDate start,
                                           LocalDate end) {
        return stockPriceService.getPriceHistory(
                symbol,
                start,
                end,
                StandardPeriodType.DAY
        );
    }

    public Map<LocalDate, BigDecimal> getDividends(String symbol,
                                                   LocalDate start,
                                                   LocalDate end) {
        return dividendService.getHistoricalDividends(symbol, start, end);
    }
}
