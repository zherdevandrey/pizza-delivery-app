package com.example.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-streams-config")
public class KafkaStreamsConfigData {
    private String applicationID;
    private String inputTopicName;
    private String outputTopicName;
    private String stateFileLocation;
    private String wordCountStoreName;
}
