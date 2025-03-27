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

        log.info("Index {}: shortMA={}, longMA={}, RSI={}, MACD={}",
                index, shortMA, longMA, rsiValue, macdValue);

        if (shortMA > longMA && rsiValue < 30 && macdValue > 0) {
            log.info("BUY signal triggered");
            return Signal.BUY;
        } else if (shortMA < longMA && rsiValue > 70 && macdValue < 0) {
            log.info("SELL signal triggered");
            return Signal.SELL;
        }

        return Signal.NEUTRAL;
    }

    public static String getFinalSignal(List<String> signals) {
        int buyCount = 0;
        int sellCount = 0;
        int neutralCount = 0;

        for (String signal : signals) {
            if (signal.equalsIgnoreCase(Signal.BUY.toString())) {
                buyCount++;
            } else if (signal.equalsIgnoreCase(Signal.SELL.toString())) {
                sellCount++;
            } else {
                neutralCount++;
            }
        }

        // Determine the aggregate signal based on the counts
        if (buyCount > sellCount && buyCount > neutralCount) {
            return Signal.BUY.toString();
        } else if (sellCount > buyCount && sellCount > neutralCount) {
            return Signal.SELL.toString();
        }
        return Signal.NEUTRAL.toString(); // return NEUTRAL if balanced or mostly neutral
    }
    public static String getLatestSignal(List<String> signals) {
        return signals.get(signals.size() - 1);
    }
}