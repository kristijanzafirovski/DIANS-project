package com.example.diansproject.service.processing;

import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.service.ingest.DataIngestService;
import com.example.diansproject.service.storage.DataStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RealTimeProcessingService {

    @Autowired
    private DataIngestService dataIngestService;
    @Autowired
    private DataStorageService dataStorageService;

    public void processAndStoreData(String symbol) {
        Map<LocalDate, DailyStockData> dailyData = dataIngestService.fetchDailyData(symbol);
        Map<LocalDateTime, IntradayStockData> intradayData = dataIngestService.fetchFiveMinuteData(symbol);
        if ((dailyData != null && !dailyData.isEmpty()) || (intradayData != null && !intradayData.isEmpty())) {
            dataStorageService.saveOrUpdateStock(symbol, dailyData, intradayData);
        } else {
            throw new RuntimeException("No data available from Twelve Data for symbol: " + symbol);
        }
    }

    public Map<String, Object> getProcessedStock(String symbol) {
        return dataStorageService.fetchData(symbol);
    }
}