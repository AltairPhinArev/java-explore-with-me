package ru.practicum.ewmmainservice.config;

import gateway.Client.StatsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static contstants.ConstantUtil.STAT_SERVICE_URL;

@Configuration
public class StatsClientConfiguration {

    @Bean
    StatsClient statsClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatsClient("http://localhost:9090", builder);
    }
}