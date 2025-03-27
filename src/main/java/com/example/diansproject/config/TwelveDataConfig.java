package com.example.diansproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TwelveDataConfig {

    @Value("${twelvedata.api_key}")
    private String apiKey;

    @Bean
    public String twelveDataApiKey() {
        return apiKey; // Provide the Twelve Data API key to other services
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // RestTemplate for API integration
    }
}