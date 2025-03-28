package com.example.diansproject.service.storage;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import com.example.diansproject.service.processing.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

        // Sort daily time series by date descending
        Map<LocalDate, DailyStockData> sortedDailyTimeSeries = stock.getTimeSeries().entrySet().stream()
                .sorted(Map.Entry.<LocalDate, DailyStockData>comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        // Sort intraday time series by date descending
        return Map.of(
                "dailyTimeSeries", sortedDailyTimeSeries,
                "intradayTimeSeries", stock.getIntradayTimeSeries(),
                "hourlyTimeSeries", stock.getHourlyTimeSeries()
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
            existingStock.setHourlyTimeSeries(hourlyTimeSeries); // Update hourly data
            stockRepository.save(existingStock);
        } else {
            Stock newStock = new Stock(symbol, LocalDate.now(), "US/Eastern", dailyTimeSeries);
            newStock.setIntradayTimeSeries(intradayTimeSeries); // Save intraday data for a new stock
            newStock.setHourlyTimeSeries(hourlyTimeSeries); // Save hourly data for a new stock
            stockRepository.save(newStock);
        }
    }
}