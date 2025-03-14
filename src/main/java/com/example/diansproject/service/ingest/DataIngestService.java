package com.example.diansproject.service.ingest;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataIngestService {
    @Autowired
    private AlphaVantage alphaVantageClient;

    public List<StockUnit> fetchData(String ticker) {
        TimeSeriesResponse response = alphaVantageClient.timeSeries().daily().forSymbol(ticker).fetchSync();
        return response.getStockUnits();
    }
}
