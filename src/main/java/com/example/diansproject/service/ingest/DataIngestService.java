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
    @Autowired
    private AlphaVantage alphaVantageClient;
    @Autowired
    private StockRepository stockRepository;

    public List<StockUnit> fetchData(String ticker) {
        // Check if stock is updated today
        if (stockRepository.findBySymbol(ticker).isEmpty()) {
            log.info("No stock found for symbol: " + ticker);
            TimeSeriesResponse response = alphaVantageClient.timeSeries().daily().forSymbol(ticker).fetchSync();
            return response.getStockUnits();
        } else {
            log.info("Stock found for symbol: " + ticker);
            TimeSeriesResponse response = alphaVantageClient.timeSeries().daily().forSymbol(ticker).fetchSync();
            return response.getStockUnits();
        }
    }
    public List<StockUnit> fetchHourlyIntradayData(String ticker) {
        TimeSeriesResponse response = alphaVantageClient.timeSeries().intraday()
                .forSymbol(ticker)
                .interval(Interval.SIXTY_MIN)
                .outputSize(OutputSize.FULL)
                .onFailure(e->handleFailure(e))
                .fetchSync();
        return response.getStockUnits();
    }
    public List<StockUnit> fetchIntradayData(String ticker) {

            TimeSeriesResponse response = alphaVantageClient.timeSeries().intraday().forSymbol(ticker).
                    interval(Interval.FIVE_MIN).outputSize(OutputSize.FULL).
                    onFailure(e->handleFailure(e)).fetchSync();
            return response.getStockUnits();

    }

    private void handleFailure(AlphaVantageException e){

    }
}
