package ru.practicum.ewmmainservice.config;

import gateway.Client.StatsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StatsClientConfiguration {

    @Value("${stats-service.url}")
    private String url;

    @Bean
    StatsClient statsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatsClient(url, builder);
    }
}