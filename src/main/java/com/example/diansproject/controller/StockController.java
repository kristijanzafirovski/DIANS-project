package com.example.diansproject.controller;

import com.example.diansproject.model.StockAnalysis;
import com.example.diansproject.service.processing.AnalysisService;
import com.example.diansproject.service.storage.DataStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StockController {


    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private DataStorageService dataStorageService;

    @GetMapping("/stocks/{symbol}")
    public Map<String, Object> getStockData(@PathVariable String symbol) {

        return dataStorageService.fetchData(symbol);
    }

    @GetMapping("/analyze/{symbol}")
    public Map<String, String> analyzeStock(@PathVariable String symbol) {
        StockAnalysis analysis = analysisService.analyze(symbol);

        Map<String, String> signals = new HashMap<>();
        signals.put("latestDailySignal", analysis.getLatestDailySignal());
        signals.put("latestIntradaySignal", analysis.getLatestIntradaySignal());
        signals.put("latestHourlySignal", analysis.getLatestHourlySignal());
        return signals;
    }
}
