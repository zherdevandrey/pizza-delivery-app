package com.example.telegram.to.kafka.app;

import com.example.kafka.admin.config.client.KafkaAdminClient;
import com.example.telegram.to.kafka.app.init.KafkaInitializer;
import com.example.telegram.to.kafka.app.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
@EnableDiscoveryClient
@RequiredArgsConstructor
@SpringBootApplication
@ComponentScan("com.example")
public class TelegramToKafkaAppApplication implements ApplicationListener<ContextRefreshedEvent> {

    private final StreamRunner streamRunner;
    private final KafkaInitializer kafkaInitializer;
    private final KafkaAdminClient kafkaAdminClient;

    public static void main(String[] args) {
        SpringApplication.run(TelegramToKafkaAppApplication.class, args);
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        kafkaInitializer.init();
        streamRunner.start();
    }
}
