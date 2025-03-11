package com.example.diansproject.config;

import com.tecacet.finance.service.stock.DividendService;
import com.tecacet.finance.service.stock.StockPriceService;
import com.tecacet.finance.service.stock.yahoo.YahooDividendService;
import com.tecacet.finance.service.stock.yahoo.YahooStockPriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceConfig {

    @Bean
    public StockPriceService yahooStockPriceService() {
        return new YahooStockPriceService();
    }

    @Bean
    public DividendService yahooDividendService() {
        return new YahooDividendService();
    }
}
