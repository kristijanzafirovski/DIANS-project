package com.example.diansproject.service.ingest;

import com.example.diansproject.model.DailyStockData;
import com.example.diansproject.model.IntradayStockData;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DataIngestService {

    private static final String BASE_URL = "https://api.twelvedata.com";
    private final RestTemplate restTemplate;
    private final String apiKey;

    @Autowired
    public DataIngestService(RestTemplate restTemplate, String twelveDataApiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = twelveDataApiKey;
    }

    /**
     * Fetches 5-minute stock data for a given symbol from the Twelve Data API.
     *
     * @param symbol The stock symbol to fetch data for.
     * @return A map of timestamps with corresponding stock data for the 5-minute interval.
     */
    public Map<LocalDateTime, IntradayStockData> fetchFiveMinuteData(String symbol) {
        String url = String.format("%s/time_series?symbol=%s&interval=5min&apikey=%s", BASE_URL, symbol, apiKey);
        log.info("Fetching 5-minute data from URL: {}", url);

        try {
            TwelveDataResponse response = restTemplate.getForObject(url, TwelveDataResponse.class);
            if (response == null || response.getValues() == null) {
                throw new RuntimeException("Failed to fetch 5-minute data; response is null.");
            }
            return transformIntradayData(response.getValues()); // Reusable transformation method
        } catch (Exception e) {
            log.error("Error fetching 5-minute data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch 5-minute stock data", e);
        }
    }

    /**
     * Fetches hourly stock data for a given symbol from the Twelve Data API.
     *
     * @param symbol The stock symbol to fetch data for.
     * @return A map of timestamps with corresponding stock data for the hourly interval.
     */
    public Map<LocalDateTime, IntradayStockData> fetchHourlyData(String symbol) {
        String url = String.format("%s/time_series?symbol=%s&interval=1h&apikey=%s", BASE_URL, symbol, apiKey);
        log.info("Fetching hourly data from URL: {}", url);

        try {
            TwelveDataResponse response = restTemplate.getForObject(url, TwelveDataResponse.class);
            if (response == null || response.getValues() == null) {
                throw new RuntimeException("Failed to fetch hourly data; response is null.");
            }
            return transformIntradayData(response.getValues()); // Reusable transformation method
        } catch (Exception e) {
            log.error("Error fetching hourly data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch hourly stock data", e);
        }
    }

    public Map<LocalDate, DailyStockData> fetchDailyData(String symbol) {
        String url = String.format("%s/time_series?symbol=%s&interval=1day&apikey=%s", BASE_URL, symbol, apiKey);
        log.info("Fetching daily data from URL: {}", url);

        try {
            TwelveDataResponse response = restTemplate.getForObject(url, TwelveDataResponse.class);
            if (response == null || response.getValues() == null) {
                throw new RuntimeException("Failed to fetch daily data; response is null.");
            }
            return transformToIntervalStockData(response.getValues()); // Reusable transformation method
        } catch (Exception e) {
            log.error("Error fetching daily data: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch daily stock data", e);
        }
    }
    /**
     * Transforms interval-based stock data into a consumable format (e.g., a map of LocalDate to DailyStockData).
     *
     * @param values The raw response values from the Twelve Data API.
     * @return A map of LocalDate to DailyStockData.
     */
    private Map<LocalDate, DailyStockData> transformToIntervalStockData(List<TwelveDataResponse.Value> values) {
        Map<LocalDate, DailyStockData> dailyStockDataMap = new HashMap<>();
        for (TwelveDataResponse.Value value : values) {
            LocalDate date = LocalDate.parse(value.getDatetime()); // parse date
            DailyStockData dailyStockData = new DailyStockData(
                    new BigDecimal(value.getOpen()),
                    new BigDecimal(value.getHigh()),
                    new BigDecimal(value.getLow()),
                    new BigDecimal(value.getClose()),
                    Long.parseLong(value.getVolume())
            );
            dailyStockDataMap.put(date, dailyStockData);
        }
        return dailyStockDataMap;
    }

    private Map<LocalDateTime, IntradayStockData> transformIntradayData(List<TwelveDataResponse.Value> values) {
        Map<LocalDateTime, IntradayStockData> intradayStockDataMap = new HashMap<>();
        for (TwelveDataResponse.Value value : values) {
            IntradayStockData intradayStockData = new IntradayStockData();
            intradayStockData.setOpen(Double.parseDouble(value.getOpen()));
            intradayStockData.setHigh(Double.parseDouble(value.getHigh()));
            intradayStockData.setLow(Double.parseDouble(value.getLow()));
            intradayStockData.setClose(Double.parseDouble(value.getClose()));
            intradayStockData.setVolume(Long.parseLong(value.getVolume()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(value.getDatetime(), formatter);
            intradayStockDataMap.put(localDateTime, intradayStockData);
        }
        return intradayStockDataMap;
    }
    // Inner class to parse Twelve Data's JSON response
    @Setter
    @Getter
    public static class TwelveDataResponse {
        private List<Value> values;

        public static class Value {
            private String datetime;
            private String open;
            private String high;
            private String low;
            private String close;
            private String volume;

            public String getDatetime() {
                return datetime;
            }

            public void setDatetime(String datetime) {
                this.datetime = datetime;
            }

            public String getOpen() {
                return open;
            }

            public void setOpen(String open) {
                this.open = open;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getClose() {
                return close;
            }

            public void setClose(String close) {
                this.close = close;
            }

            public String getVolume() {
                return volume;
            }

            public void setVolume(String volume) {
                this.volume = volume;
            }
        }
    }
}