package com.example.diansproject.service.storage;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class DataStorageService {

    private final StockRepository stockRepository;

    @Autowired
    public DataStorageService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Map<String, Object> fetchData(String ticker) {
        List<Stock> stocks = stockRepository.findBySymbol(ticker);
        if (stocks.isEmpty()) return Map.of();

        Stock stock = stocks.get(0);
        return Map.of(
                "dailyTimeSeries", stock.getTimeSeries(),
                "intradayTimeSeries", stock.getIntradayTimeSeries()
        );
    }

    public void saveOrUpdateStock(String symbol, Map<LocalDate, DailyStockData> dailyTimeSeries,
                                  Map<LocalDateTime, IntradayStockData> intradayTimeSeries,
                                  Map<LocalDateTime, IntradayStockData> hourlyTimeSeries) {
        List<Stock> existingStocks = stockRepository.findBySymbol(symbol);
        if (!existingStocks.isEmpty()) {
            Stock existingStock = existingStocks.get(0);
            existingStock.setTimeSeries(dailyTimeSeries); // Update daily data
            existingStock.setIntradayTimeSeries(intradayTimeSeries); // Update intraday data
            existingStock.setHourlyTimeSeries(hourlyTimeSeries);
            stockRepository.save(existingStock);
        } else {
            Stock newStock = new Stock(symbol, LocalDate.now(), "US/Eastern", dailyTimeSeries);
            newStock.setIntradayTimeSeries(intradayTimeSeries);
            stockRepository.save(newStock);
        }
    }
}