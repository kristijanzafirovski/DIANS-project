package com.example.diansproject.service.processing;

import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Signal;
import com.example.diansproject.service.ingest.DataIngestService;
import com.example.diansproject.service.storage.DataStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SignalGenerationService {

    private final DataStorageService dataStorageService;

    public SignalGenerationService(DataStorageService dataStorageService) {
        this.dataStorageService = dataStorageService;
    }

    //Signal generation
    public List<Signal> generateSignalsFromHourlyData(String symbol) {
        // Fetch hourly stock data for the symbol
        Map<String, Object> stockData = dataStorageService.fetchData(symbol);

        @SuppressWarnings("unchecked")
        Map<LocalDateTime, IntradayStockData> hourlyData =
                (Map<LocalDateTime, IntradayStockData>) stockData.get("hourlyTimeSeries");

        // Check if data is available
        if (hourlyData == null || hourlyData.isEmpty()) {
            log.warn("No hourly data available for symbol: {}", symbol);
            return Collections.emptyList();
        }

        // Convert hourly data to a list of Bars
        List<Bar> bars = createBarsFromHourlyData(hourlyData);

        // Create a BarSeries
        BarSeries series = new BaseBarSeriesBuilder().withName(symbol + " (Hourly)").build();
        bars.stream().sorted(Comparator.comparing(Bar::getEndTime)).forEach(series::addBar);

        // Define a trading strategy
        Strategy strategy = createSimpleMovingAverageStrategy(series);

        // Generate and return signals
        return generateSignals(series, strategy);
    }

    public List<Signal> generateSignalsFromDailyData(String symbol) {
        // Fetch daily stock data for the symbol
        Map<String, Object> stockData = dataStorageService.fetchData(symbol);
        @SuppressWarnings("unchecked")
        Map<LocalDate, DailyStockData> dailyData =
                (Map<LocalDate, DailyStockData>) stockData.get("dailyTimeSeries");


        // Check if data is available
        if (dailyData == null || dailyData.isEmpty()) {
            log.warn("No daily data available for symbol: {}", symbol);
            return Collections.emptyList();
        }

        // Convert daily data to a list of Bars
        List<Bar> bars = createBarsFromDailyData(dailyData);

        // Create a BarSeries
        BarSeries series = new BaseBarSeriesBuilder().withName(symbol + " (Daily)").build();
        bars.stream().sorted(Comparator.comparing(Bar::getEndTime)).forEach(series::addBar);

        // Define a trading strategy
        Strategy strategy = createSimpleMovingAverageStrategy(series);

        // Generate and return signals
        return generateSignals(series, strategy);
    }

    public List<Signal> generateSignalsFromIntradayData(String symbol) {
        // Fetch intraday stock data for the symbol
        Map<String, Object> stockData = dataStorageService.fetchData(symbol);
        @SuppressWarnings("unchecked")
        Map<LocalDateTime, IntradayStockData> intradayData =
                (Map<LocalDateTime, IntradayStockData>) stockData.get("intradayTimeSeries");

        // Check if data is available
        if (intradayData == null || intradayData.isEmpty()) {
            log.warn("No intraday data available for symbol: {}", symbol);
            return Collections.emptyList();
        }

        // Convert intraday data to a list of Bars
        List<Bar> bars = createBarsFromIntradayData(intradayData);

        // Create a BarSeries
        BarSeries series = new BaseBarSeriesBuilder().withName(symbol + " (Intraday)").build();
        bars.stream().sorted(Comparator.comparing(Bar::getEndTime)).forEach(series::addBar);

        // Define a trading strategy
        Strategy strategy = createSimpleMovingAverageStrategy(series);

        // Generate and return signals
        return generateSignals(series, strategy);
    }


    //Signal
    private Strategy createSimpleMovingAverageStrategy(BarSeries series) {
        // Define indicators
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSMA = new SMAIndicator(closePrice, 5);
        SMAIndicator longSMA = new SMAIndicator(closePrice, 20);

        // Define entry and exit rules
        Rule entryRule = new CrossedUpIndicatorRule(shortSMA, longSMA); // Short SMA crosses above Long SMA
        Rule exitRule = new CrossedDownIndicatorRule(shortSMA, longSMA); // Short SMA crosses below Long SMA

        return new BaseStrategy(entryRule, exitRule);
    }

    private List<Signal> generateSignals(BarSeries series, Strategy strategy) {
        List<Signal> signals = new ArrayList<>();
        TradingRecord tradingRecord = new BaseTradingRecord(); // Keeps track of strategy trades

        for (int i = 0; i < series.getBarCount(); i++) {
            if (strategy.shouldEnter(i, tradingRecord)) {
                signals.add(Signal.BUY);
                tradingRecord.enter(i, series.getBar(i).getClosePrice(), series.getBar(i).getVolume());
                log.info("BUY signal generated at index {} for bar {}", i, series.getBar(i));
            } else if (strategy.shouldExit(i, tradingRecord)) {
                signals.add(Signal.SELL);
                tradingRecord.exit(i, series.getBar(i).getClosePrice(), series.getBar(i).getVolume());
                log.info("SELL signal generated at index {} for bar {}", i, series.getBar(i));
            }
        }

        return signals;
    }


    //Conversion to bars
    private List<Bar> createBarsFromDailyData(Map<LocalDate, DailyStockData> dailyData) {
        // Define the time zone explicitly (e.g., UTC or a specific time zone)
        java.time.ZoneId zoneId = java.time.ZoneId.of("UTC");

        return dailyData.entrySet().stream()
                .map(entry -> {
                    DailyStockData data = entry.getValue();
                    // Convert LocalDate to ZonedDateTime with the specified zone
                    ZonedDateTime zonedDateTime = entry.getKey().atStartOfDay(zoneId);
                    return new BaseBar(
                            Duration.ofDays(1),
                            zonedDateTime,
                            data.getOpen(),
                            data.getHigh(),
                            data.getLow(),
                            data.getClose(),
                            BigDecimal.valueOf(data.getVolume())
                    );
                })
                .collect(Collectors.toList());
    }

    private List<Bar> createBarsFromIntradayData(Map<LocalDateTime, IntradayStockData> intradayData) {
        // Define the time zone explicitly (e.g., UTC or another specific time zone)
        java.time.ZoneId zoneId = java.time.ZoneId.of("UTC");

        return intradayData.entrySet().stream()
                .map(entry -> {
                    IntradayStockData data = entry.getValue();
                    // Convert LocalDateTime to ZonedDateTime with a specific ZoneId
                    ZonedDateTime zonedDateTime = entry.getKey().atZone(zoneId);
                    return new BaseBar(Duration.ofMinutes(5), zonedDateTime,
                            data.getOpen(), data.getHigh(), data.getLow(), data.getClose(), data.getVolume());
                })
                .collect(Collectors.toList());
    }

    private List<Bar> createBarsFromHourlyData(Map<LocalDateTime, IntradayStockData> hourlyData) {
        // Define the time zone explicitly (e.g., UTC or another specific time zone)
        java.time.ZoneId zoneId = java.time.ZoneId.of("UTC");

        return hourlyData.entrySet().stream()
                .map(entry -> {
                    IntradayStockData data = entry.getValue();
                    // Convert LocalDateTime to ZonedDateTime with a specific ZoneId
                    ZonedDateTime zonedDateTime = entry.getKey().atZone(zoneId);
                    return new BaseBar(Duration.ofHours(1), zonedDateTime,
                            data.getOpen(), data.getHigh(), data.getLow(), data.getClose(), data.getVolume());
                }).collect(Collectors.toList());
    }
}