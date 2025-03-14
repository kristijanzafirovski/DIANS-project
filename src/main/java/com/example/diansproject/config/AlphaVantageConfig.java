package com.example.diansproject.config;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlphaVantageConfig {

    @Value("${api_key}")
    private String key;

    @Bean
    public AlphaVantage alphaVantageClient() {
        Config config = Config.builder().key(key).timeOut(10).build();
        AlphaVantage.api().init(config);
        return AlphaVantage.api();
    }


}
