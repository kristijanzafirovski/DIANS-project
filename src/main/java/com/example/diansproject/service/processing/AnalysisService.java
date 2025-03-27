package com.example.diansproject.service.processing;

import com.example.diansproject.service.ingest.DataIngestService;
import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Signal;
import com.example.diansproject.model.StockAnalysis;
import com.example.diansproject.service.storage.DataStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnalysisService {

    private final DataIngestService dataIngestService;
    private final SignalGenerationService signalGenerationService;
    private final DataStorageService dataStorageService;

    public AnalysisService(DataIngestService dataIngestService, SignalGenerationService signalGenerationService, DataStorageService dataStorageService) {
        this.dataIngestService = dataIngestService;
        this.signalGenerationService = signalGenerationService;
        this.dataStorageService = dataStorageService;
    }

    //Entrypoint of /analyze
    public StockAnalysis analyze(String symbol) {
        log.info("Starting analysis for stock symbol: {}", symbol);

        //Get data from db, or ingest if empty
        Map<String, Object> storedData = dataStorageService.fetchData(symbol);
        if(storedData.isEmpty()) {
            dataIngestService.ingestData(symbol);
            //get from db after ingest
            storedData = dataStorageService.fetchData(symbol);
        }else {
            log.info("Data for symbol: {} found in storage.", symbol);
        }

        //Get daily and intraday data
        Map<LocalDate, DailyStockData> dailyStockData = (Map<LocalDate, DailyStockData>) storedData.get("dailyTimeSeries");
        Map<LocalDateTime, IntradayStockData> intradayStockData = (Map<LocalDateTime, IntradayStockData>) storedData.get("intradayTimeSeries");
        Map<LocalDateTime, IntradayStockData> hourlyStockData = (Map<LocalDateTime, IntradayStockData>) storedData.get("hourlyTimeSeries");


        // Generate signals using SignalGenerationService
        List<Signal> dailySignals = signalGenerationService.generateSignalsFromDailyData(symbol);
        List<Signal> intradaySignals = signalGenerationService.generateSignalsFromIntradayData(symbol);
        List<Signal> hourlySignals = signalGenerationService.generateSignalsFromHourlyData(symbol);


        String latestDailySignal = extractLatestSignal(dailySignals);
        String latestIntradaySignal = extractLatestSignal(intradaySignals);
        String latestHourlySignal = extractLatestSignal(hourlySignals);

        // Create and return a StockAnalysis result
        return buildStockAnalysis(symbol, latestDailySignal, latestIntradaySignal,latestHourlySignal, dailyStockData, intradayStockData);
    }

    /**
     * Helper method to extract the latest signal from a list of signals.
     */
    private String extractLatestSignal(List<Signal> signals) {
        if (signals == null || signals.isEmpty()) {
            return Signal.NEUTRAL.toString();
        }
        return signals.get(signals.size() - 1).toString(); // Assume the last signal is the most recent
    }

    /**
     * Builds the StockAnalysis object from the provided data.
     */
    private StockAnalysis buildStockAnalysis(String symbol, String dailySignal, String intradaySignal,String hourlySignal,
                                             Map<LocalDate, DailyStockData> dailyStockData,
                                             Map<LocalDateTime, IntradayStockData> intradayStockData) {
        log.info("Building stock analysis for {}: [Daily Signal: {}, Intraday Signal: {}]", symbol, dailySignal, intradaySignal);

        StockAnalysis analysis = new StockAnalysis();
        analysis.setSymbol(symbol);
        analysis.setLatestDailySignal(dailySignal);
        analysis.setLatestHourlySignal(hourlySignal);
        analysis.setLatestIntradaySignal(intradaySignal);
        analysis.setDailyData(dailyStockData);
        analysis.setIntradayData(intradayStockData);

        return analysis;
    }
}