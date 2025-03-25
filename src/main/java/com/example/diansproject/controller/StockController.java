package com.example.diansproject.controller;

import com.example.diansproject.model.Stock;
import com.example.diansproject.model.StockAnalysis;
import com.example.diansproject.service.processing.AnalysisService;
import com.example.diansproject.service.processing.RealTimeProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StockController {

    @Autowired
    private RealTimeProcessingService realTimeProcessingService;

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/stocks/{symbol}")
    public List<Stock> getStockData(@PathVariable String symbol) {
        // This will trigger the data ingestion, processing, and storage flow
        realTimeProcessingService.processAndStoreData(symbol);
        // Return the processed stock data
        return realTimeProcessingService.getProcessedStock(symbol);
    }

    @GetMapping("/analyze/{symbol}")
    public StockAnalysis analyzeStock(@PathVariable String symbol) {
        return analysisService.analyze(symbol);
    }
}
