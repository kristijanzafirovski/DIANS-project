package com.example.diansproject.service.processing;

import com.example.diansproject.model.IntradayStockData;
import com.example.diansproject.model.Signal;
import com.example.diansproject.model.StockAnalysis;
import com.example.diansproject.model.DailyStockData;
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
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AnalysisService {

    private final DataIngestService dataIngestService;

    public AnalysisService(DataIngestService dataIngestService) {
        this.dataIngestService = dataIngestService;
    }

    /**
     * Analyzes stock prices using different time data intervals (intraday, daily, hourly).
     *
     * @param symbol the ticker symbol of the stock
     * @return an analysis report containing signals for the various timeframes
     */
    public StockAnalysis analyze(String symbol) {
        log.info("Starting analysis for symbol: {}", symbol);

        // Fetch the data from Twelve Data API
        Map<LocalDate, DailyStockData> dailyData = dataIngestService.fetchDailyData(symbol);
        Map<LocalDateTime, IntradayStockData> fiveMinuteData = dataIngestService.fetchFiveMinuteData(symbol);
        Map<LocalDateTime, IntradayStockData> hourlyData = dataIngestService.fetchHourlyData(symbol);

        // Generate signals for each timeframe
        String dailySignal = generateSignalFromDailyData(dailyData);
        String intradaySignal = generateSignalFromIntradayOrHourlyData(fiveMinuteData, "intraday", false);
        String hourlySignal = generateSignalFromIntradayOrHourlyData(hourlyData, "hourly", true);

        return new StockAnalysis(intradaySignal, dailySignal, hourlySignal);
    }

    /**
     * Generates a signal based on daily stock data.
     *
     * @param dailyStockDataMap A map of daily stock data
     * @return Signal as a String
     */
    private String generateSignalFromDailyData(Map<LocalDate, DailyStockData> dailyStockDataMap) {
        if (dailyStockDataMap == null || dailyStockDataMap.isEmpty()) {
            log.warn("No {} data available for signal generation.", "daily");
            return Signal.NEUTRAL.toString();
        }

        List<Bar> bars = createBarsFromDailyData(dailyStockDataMap);
        return computeSignal(bars);
    }

    /**
     * Generates a signal based on intraday or hourly stock data.
     *
     * @param stockDataMap A map of string timestamps to stock data
     * @param timeframe    The timeframe of analysis
     * @return Signal as a String
     */
    private String generateSignalFromIntradayOrHourlyData(Map<LocalDateTime, IntradayStockData> stockDataMap, String timeframe, boolean isHourly) {
        if (stockDataMap == null || stockDataMap.isEmpty()) {
            log.warn("No {} data available for signal generation.", timeframe);
            return Signal.NEUTRAL.toString();
        }

        List<Bar> bars = createBarsFromIntradayOrHourlyData(stockDataMap, isHourly);
        return computeSignal(bars);
    }

    /**
     * Transforms daily stock data into Ta4j bars.
     *
     * @param stockDataMap A map of daily stock data
     * @return A list of Ta4j bars
     */
    private List<Bar> createBarsFromDailyData(Map<LocalDate, DailyStockData> stockDataMap) {
        List<Bar> bars = new ArrayList<>();
        for (Map.Entry<LocalDate, DailyStockData> entry : stockDataMap.entrySet()) {
            LocalDate date = entry.getKey();
            DailyStockData stockData = entry.getValue();

            Bar bar = new BaseBar(
                    Duration.ofDays(1),
                    ZonedDateTime.of(date.atStartOfDay(), ZoneId.systemDefault()),
                    stockData.getOpen(),
                    stockData.getHigh(),
                    stockData.getLow(),
                    stockData.getClose(),
                    BigDecimal.valueOf(stockData.getVolume())
            );
            bars.add(bar);
        }
        return bars;
    }

    /**
     * Transforms intraday or hourly stock data into Ta4j bars.
     *
     * @param stockDataMap A map of string timestamps to stock data
     * @return A list of Ta4j bars
     */
    private List<Bar> createBarsFromIntradayOrHourlyData(Map<LocalDateTime, IntradayStockData> stockDataMap, boolean isHourly) {
        List<Bar> bars = new ArrayList<>();
        for (Map.Entry<LocalDateTime, IntradayStockData> entry : stockDataMap.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            IntradayStockData stockData = entry.getValue();
            Duration duration;
            if (isHourly){
                duration = Duration.ofHours(1);
            }else duration = Duration.ofMinutes(5);

            ZonedDateTime dateTime = timestamp.atZone(ZoneId.systemDefault());

            Bar bar = new BaseBar(
                    duration,
                    dateTime,
                    BigDecimal.valueOf(stockData.getOpen()),
                    BigDecimal.valueOf(stockData.getHigh()),
                    BigDecimal.valueOf(stockData.getLow()),
                    BigDecimal.valueOf(stockData.getClose()),
                    BigDecimal.valueOf(stockData.getVolume())
            );
            bars.add(bar);
        }
        return bars;
    }

    /**
     * Computes a signal (BUY, SELL, or NEUTRAL) based on the given bars.
     *
     * @param bars A list of Ta4j bars
     * @return Signal as a String
     */
    private String computeSignal(List<Bar> bars) {
        if (bars.isEmpty()) {
            return Signal.NEUTRAL.toString();
        }

        BarSeries series = new BaseBarSeriesBuilder().withName("Stock Data").build();

        bars.sort(Comparator.comparing(Bar::getEndTime));
        for (Bar bar : bars) {
            series.addBar(bar);
        }

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        SMAIndicator shortSMA = new SMAIndicator(closePriceIndicator, 5);
        SMAIndicator longSMA = new SMAIndicator(closePriceIndicator, 20);
        RSIIndicator rsi = new RSIIndicator(closePriceIndicator, 14);
        MACDIndicator macd = new MACDIndicator(closePriceIndicator, 12, 26);

        return SignalUtils.generateSignal(
                series.getEndIndex(),
                shortSMA,
                longSMA,
                rsi,
                macd
        ).toString();
    }
}