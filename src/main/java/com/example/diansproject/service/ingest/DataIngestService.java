package com.example.diansproject.service.ingest;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DataIngestService {
    @Autowired
    private AlphaVantage alphaVantageClient;
    @Autowired
    private StockRepository stockRepository;

    public List<StockUnit> fetchData(String ticker) {
        // Check if stock is updated today
        if (stockRepository.findBySymbol(ticker).isEmpty()) {
            TimeSeriesResponse response = alphaVantageClient.timeSeries().daily().forSymbol(ticker).fetchSync();
            return response.getStockUnits();
        } else {
            Stock stock = stockRepository.findBySymbol(ticker).get(0);
            if (!LocalDate.parse(stock.getLastRefreshed()).equals(LocalDate.now())) {
                TimeSeriesResponse response = alphaVantageClient.timeSeries().daily().forSymbol(ticker).fetchSync();
                return response.getStockUnits();
            } else {
                return null;
            }
        }
    }
}
