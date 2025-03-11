package com.example.diansproject.config;

import com.tecacet.finance.model.Quote;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "stockHistory",
                "dividendHistory"
        );
    }


    // Add caching to service methods
    @Cacheable("stockHistory")
    public List<Quote> getHistoricalPrices(String symbol, LocalDate start, LocalDate end) {
        // ... existing implementation
        return null;
    }

    @Cacheable("dividendHistory")
    public Map<LocalDate, BigDecimal> getDividends(String symbol, LocalDate start, LocalDate end) {
        // ... existing implementation
        return null;
    }
}