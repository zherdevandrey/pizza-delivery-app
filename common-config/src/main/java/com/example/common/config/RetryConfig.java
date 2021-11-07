package com.example.common.config;

import com.example.app.config.data.RetryConfigData;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@RequiredArgsConstructor
public class RetryConfig {

    private final RetryConfigData retryConfigData;

    @Bean
    public RetryTemplate retryTemplate(){
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(retryConfigData.getInitialIntervalMs());
        policy.setMaxInterval(retryConfigData.getMaxIntervalMs());
        policy.setMultiplier(retryConfigData.getMultiplier());
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(policy);
        return retryTemplate;
    }
}
