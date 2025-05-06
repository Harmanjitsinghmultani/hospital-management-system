package com.example.appointment_service.config;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import org.springframework.cloud.netflix.eureka.EurekaHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaHealthConfig {

    @Bean
    public EurekaHealthIndicator eurekaHealthIndicator(
            EurekaClient eurekaClient,
            EurekaInstanceConfig eurekaInstanceConfig,
            EurekaClientConfig eurekaClientConfig) {
        return new EurekaHealthIndicator(eurekaClient, eurekaInstanceConfig, eurekaClientConfig);
    }
}