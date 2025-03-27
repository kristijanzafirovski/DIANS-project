package com.example.diansproject.service.ingest;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.AlphaVantageException;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import com.example.diansproject.model.Stock;
import com.example.diansproject.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class DataIngestService {

    private final AlphaVantage alphaVantageClient;
    private final StockRepository stockRepository;

    @Autowired
    public DataIngestService(AlphaVantage alphaVantageClient, StockRepository stockRepository) {
        this.alphaVantageClient = alphaVantageClient;
        this.stockRepository = stockRepository;
    }

    public List<StockUnit> fetchData(String ticker) {
        return fetchStockUnits(() -> alphaVantageClient.timeSeries().daily().outputSize(OutputSize.FULL).forSymbol(ticker).fetchSync());
    }

    public List<StockUnit> fetchHourlyIntradayData(String ticker) {
        return fetchStockUnits(() -> alphaVantageClient.timeSeries()
                .intraday()
                .forSymbol(ticker)
                .interval(Interval.SIXTY_MIN)
                .outputSize(OutputSize.FULL)
                .fetchSync());
    }

    public List<StockUnit> fetchIntradayData(String ticker) {
        return fetchStockUnits(() -> alphaVantageClient.timeSeries()
                .intraday()
                .forSymbol(ticker)
                .interval(Interval.FIVE_MIN)
                .outputSize(OutputSize.FULL)
                .fetchSync());
    }

    private List<StockUnit> fetchStockUnits(DataFetchOperation operation) {
        try {
            return operation.fetch().getStockUnits();
        } catch (AlphaVantageException e) {
            log.error("Failed to fetch stock data: {}", e.getMessage());
            return List.of();
        }
    }

    private interface DataFetchOperation {
        TimeSeriesResponse fetch();
    }
}