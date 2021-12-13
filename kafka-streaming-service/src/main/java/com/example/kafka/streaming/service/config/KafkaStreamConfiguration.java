package com.example.kafka.streaming.service.config;

import com.example.app.config.data.KafkaConfigData;
import com.example.app.config.data.KafkaStreamsConfigData;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "com.example")
public class KafkaStreamConfiguration {

    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final KafkaConfigData kafkaConfigData;

    @Bean
    @Primary
    @Qualifier("streamConfiguration")
    public Properties streamConfiguration() {
        Properties streamConfiguration = new Properties();
        streamConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamsConfigData.getApplicationID());
        streamConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        streamConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfigData.getSchemaRegistryUrl());
        streamConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStreamsConfigData.getStateFileLocation());
        return streamConfiguration;
    }

}
