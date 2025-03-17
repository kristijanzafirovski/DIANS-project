package com.example.diansproject.service.processing;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.service.ingest.DataIngestService;
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {
    private final DataIngestService dataIngestService;

    public AnalysisService(DataIngestService dataIngestService) {
        this.dataIngestService = dataIngestService;
    }

    public void analyze(String symbol) {
        List<StockUnit> intradayData = dataIngestService.fetchIntradayData(symbol);
        List<StockUnit> dailyData = dataIngestService.fetchData(symbol);

        List<Bar> intradayBarSeries = createBars(intradayData);
        List<Bar> dailySeries = createBars(dailyData);

        analyzeIntraday(createBarSeries(intradayBarSeries));
        analyzeDaily(createBarSeries(dailySeries));


    }


    private List<Bar> createBars(List<StockUnit> stockUnits) {
        List<Bar> bars = new ArrayList<>();
        for (StockUnit unit : stockUnits) {
            ZonedDateTime endTime = ZonedDateTime.parse(unit.getDate());
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

    private void analyzeIntraday(BarSeries series) {
        // Calculate indicators
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 10);
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);

        // Analyze indicators
        for (int i = 0; i < series.getBarCount(); i++) {
            double shortSmaValue = shortSma.getValue(i).doubleValue();
            double longSmaValue = longSma.getValue(i).doubleValue();
            double rsiValue = rsi.getValue(i).doubleValue();
            double macdValue = macd.getValue(i).doubleValue();

            // Example analysis logic
            if (shortSmaValue > longSmaValue) {
                System.out.println("Intraday: Short SMA is above Long SMA at bar " + i);
            }
            if (rsiValue > 70) {
                System.out.println("Intraday: RSI is overbought at bar " + i);
            }
            if (macdValue > 0) {
                System.out.println("Intraday: MACD is positive at bar " + i);
            }
        }
    }

    private void analyzeDaily(BarSeries series) {
        // Calculate indicators
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 50);
        SMAIndicator longSma = new SMAIndicator(closePrice, 200);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);

        // Analyze indicators
        for (int i = 0; i < series.getBarCount(); i++) {
            double shortSmaValue = shortSma.getValue(i).doubleValue();
            double longSmaValue = longSma.getValue(i).doubleValue();
            double rsiValue = rsi.getValue(i).doubleValue();
            double macdValue = macd.getValue(i).doubleValue();

            // Example analysis logic
            if (shortSmaValue > longSmaValue) {
                System.out.println("Daily: Short SMA is above Long SMA at bar " + i);
            }
            if (rsiValue > 70) {
                System.out.println("Daily: RSI is overbought at bar " + i);
            }
            if (macdValue > 0) {
                System.out.println("Daily: MACD is positive at bar " + i);
            }
        }
    }
}
}
