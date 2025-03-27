package com.example.diansproject.service.processing;

import com.example.diansproject.model.Signal;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;

import java.util.List;
@Slf4j
public class SignalUtils {

    public static Signal generateSignal(int index, SMAIndicator shortSMA, SMAIndicator longSMA,
                                        RSIIndicator rsi, MACDIndicator macd) {
        double shortMA = shortSMA.getValue(index).doubleValue();
        double longMA = longSMA.getValue(index).doubleValue();
        double rsiValue = rsi.getValue(index).doubleValue();
        double macdValue = macd.getValue(index).doubleValue();

        if (shortMA > longMA && rsiValue < 30 && macdValue > 0) {
            log.info("BUY signal triggered");
            return Signal.BUY;
        } else if (shortMA < longMA && rsiValue > 70 && macdValue < 0) {
            log.info("SELL signal triggered");
            return Signal.SELL;
        }
        if (shortMA >= longMA * 0.98 && rsiValue <= 40 && macdValue >= -0.5) {
            log.info("BUY signal triggered under relaxed conditions");
            return Signal.BUY;
        } else if (shortMA <= longMA * 1.02 && rsiValue >= 60 && macdValue <= 0.5) {
            log.info("SELL signal triggered under relaxed conditions");
            return Signal.SELL;
        }

        return Signal.NEUTRAL;
    }

}