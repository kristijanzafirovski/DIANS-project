package com.example.diansproject.service.storage;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DataStorageService {

    @Autowired
    private final StockRepository stockRepository;

    public DataStorageService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public void saveStock(List<Stock> stocks) {
        stockRepository.saveAll(stocks);
    }
    public List<Stock> fetchData(String ticker) {
        return stockRepository.findBySymbol(ticker);
    }


}
