package com.example.diansproject.service.processing;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisService {

    private final DataIngestService dataIngestService;

    public AnalysisService(DataIngestService dataIngestService) {
        this.dataIngestService = dataIngestService;
    }

    public StockAnalysis analyze(String symbol) {
        // Fetch data for different timeframes (daily, hourly, intraday)
        List<StockUnit> dailyData = dataIngestService.fetchData(symbol);
        List<StockUnit> hourlyData = dataIngestService.fetchHourlyIntradayData(symbol);
        List<StockUnit> intradayData = dataIngestService.fetchIntradayData(symbol);

        // Generate signals for each timeframe
        String dailySignal = generateSignal(dailyData, "Daily");
        String hourlySignal = generateSignal(hourlyData, "Hourly");
        String intradaySignal = generateSignal(intradayData, "Intraday");

        return new StockAnalysis(intradaySignal, dailySignal, hourlySignal);
    }


    private String generateSignal(List<StockUnit> stockUnits, String timeframe) {
        List<Bar> bars = createBars(stockUnits);
        if (bars.isEmpty()) return Signal.NEUTRAL.toString();

        // Sort bars chronologically
        bars.sort(Comparator.comparing(Bar::getEndTime));
        bars = bars.stream().distinct().collect(Collectors.toList());

        if (bars.size() < Math.max(20, 14)) { // Ensure enough data for indicators
            return Signal.NEUTRAL.toString();
        }

        BarSeries series = new BaseBarSeriesBuilder().withName(timeframe + " Series").build();
        bars.forEach(series::addBar);

        ClosePriceIndicator closePrices = new ClosePriceIndicator(series);

        // Adjusted parameters
        SMAIndicator shortSMA = new SMAIndicator(closePrices, 5);   // Short-term MA
        SMAIndicator longSMA = new SMAIndicator(closePrices, 20);   // Long-term MA
        RSIIndicator rsi = new RSIIndicator(closePrices, 14);       // Standard RSI period
        MACDIndicator macd = new MACDIndicator(closePrices, 12, 26); // Standard MACD periods

        List<String> signals = new ArrayList<>();
        for (int i = Math.max(20, 14); i < series.getBarCount(); i++) { // Start after unstable period
            Signal signal = SignalUtils.generateSignal(i, shortSMA, longSMA, rsi, macd);
            signals.add(signal.toString());
        }

        return SignalUtils.getFinalSignal(signals);
    }

    private List<Bar> createBars(List<StockUnit> stockUnits) {
        // Define a formatter for the date-time string format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return stockUnits.stream().map(stockUnit -> {
            // Parse the date and time string to a ZonedDateTime
            ZonedDateTime dateTime = ZonedDateTime.parse(stockUnit.getDate(), formatter.withZone(ZoneId.systemDefault()));

            // Create and return a TA4J Bar
            return new BaseBar(Duration.ofDays(1), dateTime,
                    new BigDecimal(stockUnit.getOpen()),
                    new BigDecimal(stockUnit.getHigh()),
                    new BigDecimal(stockUnit.getLow()),
                    new BigDecimal(stockUnit.getClose()),
                    new BigDecimal(stockUnit.getVolume())
            );
        }).collect(Collectors.toList());
    }

}