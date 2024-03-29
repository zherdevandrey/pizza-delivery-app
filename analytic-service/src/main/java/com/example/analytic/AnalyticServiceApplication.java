package com.example.analytic;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@ComponentScan(basePackages = "com.example")
@SpringBootApplication
@RequiredArgsConstructor
public class AnalyticServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticServiceApplication.class, args);
    }

}
