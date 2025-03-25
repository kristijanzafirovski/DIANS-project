package com.example.diansproject.service.processing;

import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.example.diansproject.model.IndicatorValues;
import com.example.diansproject.model.Signal;
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
        List<StockUnit> hourlyIntradayData = dataIngestService.fetchHourlyIntradayData(symbol);
        List<StockUnit> dailyData = dataIngestService.fetchData(symbol);

        List<Bar> intradayBarSeries = createBars(intradayData);
        List<Bar> hourlyBarSeries = createBars(hourlyIntradayData);
        List<Bar> dailySeries = createBars(dailyData);

        String intradaySignal = getLatestSignal(generateSignals(createBarSeries(intradayBarSeries)));
        String hourlySignal = getLatestSignal(generateSignals(createBarSeries(hourlyBarSeries)));
        String dailySignal = getLatestSignal(generateSignals(createBarSeries(dailySeries)));

        return new StockAnalysis(intradaySignal, dailySignal, hourlySignal);
    }

    private List<Bar> createBars(List<StockUnit> stockUnits) {
        List<Bar> bars = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        for (StockUnit unit : stockUnits) {
            String str = unit.getDate().replace(" ", "T") + "Z";
            if(str.length() <= 11) continue;
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

    private List<String> generateSignals(BarSeries series) {
        List<String> signals = new ArrayList<>();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator shortSma = new SMAIndicator(closePrice, 10);
        SMAIndicator longSma = new SMAIndicator(closePrice, 30);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);

        for (int i = 0; i < series.getBarCount(); i++) {
            Signal signal = getSignal(i, shortSma, longSma, rsi, macd);
            signals.add(signal != null ? signal.toString() : "NEUTRAL");
        }

        return signals;
    }

    private Signal getSignal(int index, SMAIndicator shortSma, SMAIndicator longSma, RSIIndicator rsi, MACDIndicator macd) {
        if ((shortSma.getValue(index).doubleValue() > longSma.getValue(index).doubleValue()) &&
                (rsi.getValue(index).doubleValue() < 30) &&
                (macd.getValue(index).doubleValue() > 0)) {
            return Signal.BUY;
        }
        else if ((shortSma.getValue(index).doubleValue() < longSma.getValue(index).doubleValue()) &&
                (rsi.getValue(index).doubleValue() > 70) &&
                (macd.getValue(index).doubleValue() < 0)) {
            return Signal.SELL;
        }
        return null;
    }

    private String getLatestSignal(List<String> signals) {
        if (signals.isEmpty()) return "NEUTRAL";
        return signals.get(signals.size() - 1);
    }
}
