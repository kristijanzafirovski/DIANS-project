package com.example.diansproject.service.processing;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.model.StockAnalysis;
import com.example.diansproject.service.ingest.DataIngestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AnalysisService {
    private final DataIngestService dataIngestService;

    public AnalysisService(DataIngestService dataIngestService) {
        this.dataIngestService = dataIngestService;
    }

    public StockAnalysis analyze(String symbol) {
        List<StockUnit> intradayData = dataIngestService.fetchIntradayData(symbol);
        log.info("Analysis of " + symbol + " intraday data: " + intradayData);
        List<StockUnit> dailyData = dataIngestService.fetchData(symbol);

        List<Bar> intradayBarSeries = createBars(intradayData);
        List<Bar> dailySeries = createBars(dailyData);

        return new StockAnalysis(analyzeIntraday(createBarSeries(intradayBarSeries))
        ,analyzeDaily(createBarSeries(dailySeries)));


    }


    private List<Bar> createBars(List<StockUnit> stockUnits) {
        List<Bar> bars = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (StockUnit unit : stockUnits) {
            String str = unit.getDate().replace(" ", "T") + ":00Z";
            ZonedDateTime endTime = ZonedDateTime.parse(str, formatter);
            BaseBar bar = new BaseBar(
                    Duration.ofMinutes(5),endTime,
                    BigDecimal.valueOf(unit.getOpen()),
                    BigDecimal.valueOf(unit.getHigh()),
                    BigDecimal.valueOf(unit.getLow()),
                    BigDecimal.valueOf(unit.getClose()),
                    BigDecimal.valueOf(unit.getVolume())
                    );
            bars.add(bar);
        }
        return bars;
    }

    private BarSeries createBarSeries(List<Bar> series) {
        return new BaseBarSeriesBuilder().withBars(series).build();
    }

    private List<String> analyzeIntraday(BarSeries series) {
        List<String> signals = new ArrayList<>();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 10);
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);


        if (series.getBarCount() < 30) { // Need at least 30 bars for indicators
            log.warn("Insufficient data for intraday analysis: " + series.getBarCount() + " bars");
            return signals;
        }

        for (int i = 0; i < series.getBarCount(); i++) {
            StringBuilder signal = new StringBuilder();
            double shortSmaValue = shortSma.getValue(i).doubleValue();
            double longSmaValue = longSma.getValue(i).doubleValue();
            double rsiValue = rsi.getValue(i).doubleValue();
            double macdValue = macd.getValue(i).doubleValue();


            // Add logging for debugging
            log.info("Bar {}: SMA({} vs {}), RSI({}), MACD({})",
                    i, shortSmaValue, longSmaValue, rsiValue, macdValue);

            // Example analysis logic with logging
            if (shortSmaValue > longSmaValue) {
                signal.append("Intraday: Short SMA is above Long SMA at bar ").append(i);
                log.info("SMA Crossover detected at bar " + i);
            }
            if (rsiValue > 70) {
                signal.append("Intraday: RSI is overbought at bar ").append(i);
                log.info("RSI Overbought detected at bar " + i);
            }
            if (macdValue > 0) {
                signal.append("Intraday: MACD is positive at bar ").append(i);
                log.info("MACD Positive detected at bar " + i);
            }

            if (signal.length() > 0) {
                signals.add(signal.toString());
            }
        }
        return signals;
    }

    private List<String> analyzeDaily(BarSeries series) {
        // Calculate indicators
        List<String> signals = new ArrayList<>();
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 50);
        SMAIndicator longSma = new SMAIndicator(closePrice, 200);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);

        // Analyze indicators
        for (int i = 0; i < series.getBarCount(); i++) {
            StringBuilder signal = new StringBuilder();

            double shortSmaValue = shortSma.getValue(i).doubleValue();
            double longSmaValue = longSma.getValue(i).doubleValue();
            double rsiValue = rsi.getValue(i).doubleValue();
            double macdValue = macd.getValue(i).doubleValue();

            // Example analysis logic
            if (shortSmaValue > longSmaValue) {
                signal.append("Daily: Short SMA is above Long SMA at bar " + i);
            }
            if (rsiValue > 70) {
                signal.append("Daily: RSI is overbought at bar " + i);
            }
            if (macdValue > 0) {
                signal.append("Daily: MACD is positive at bar " + i);
            }
            signals.add(signal.toString());
        }
        return signals;
    }
}

