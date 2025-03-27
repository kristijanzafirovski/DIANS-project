package com.example.diansproject.service.storage;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
public class DataStorageService {

    private final StockRepository stockRepository;

    @Autowired
    public DataStorageService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public void saveStock(List<Stock> stocks) {
        stockRepository.saveAll(stocks);
    }

    public List<Stock> fetchData(String ticker) {
        return stockRepository.findBySymbol(ticker);
    }

    public void saveOrUpdateStock(String symbol, Map<LocalDate, DailyStockData> timeseries) {
        List<Stock> existingStocks = stockRepository.findBySymbol(symbol);
        if (!existingStocks.isEmpty()) {
            Stock existingStock = existingStocks.get(0);
            existingStock.setTimeSeries(timeseries); // Update existing stock
            stockRepository.save(existingStock);
        } else {
            Stock newStock = new Stock(symbol, LocalDate.now(), "US/Eastern", timeseries);
            stockRepository.save(newStock);
        }
    }
}