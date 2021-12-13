package com.example.kafka.streaming.service;

import com.example.kafka.streaming.service.runner.KafkaStreamsRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class KafkaStreamingServiceApplication implements CommandLineRunner {

    private final KafkaStreamsRunner kafkaStreamsRunner;

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamingServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        kafkaStreamsRunner.start();
    }
}
