package com.example.diansproject.service.processing;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.Stock;
import com.example.diansproject.service.ingest.DataIngestService;
import com.example.diansproject.service.storage.DataStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RealTimeProcessingService {

    @Autowired
    private DataIngestService dataIngestService;
    @Autowired
    private DataStorageService dataStorageService;

    public List<Stock> getProcessedStock(String symbol) {
        return dataStorageService.fetchData(symbol);
    }

    public void processAndStoreData(String symbol) {
        List<StockUnit> stockUnits = dataIngestService.fetchData(symbol);
        if (stockUnits != null) {
            Map<LocalDate, DailyStockData> timeseries = transformStockUnitsToTimeseries(stockUnits);
            saveOrUpdateStock(symbol, timeseries);
        }
    }

    private Map<LocalDate, DailyStockData> transformStockUnitsToTimeseries(List<StockUnit> stockUnits) {
        Map<LocalDate, DailyStockData> timeseries = new HashMap<>();
        for (StockUnit stockUnit : stockUnits) {
            DailyStockData dailyData = new DailyStockData(
                    new BigDecimal(stockUnit.getOpen()),
                    new BigDecimal(stockUnit.getHigh()),
                    new BigDecimal(stockUnit.getLow()),
                    new BigDecimal(stockUnit.getClose()),
                    stockUnit.getVolume()
            );
            timeseries.put(LocalDate.parse(stockUnit.getDate()), dailyData);
        }
        return timeseries;
    }

    private void saveOrUpdateStock(String symbol, Map<LocalDate, DailyStockData> timeseries) {
        dataStorageService.saveOrUpdateStock(symbol, timeseries);
    }

}
