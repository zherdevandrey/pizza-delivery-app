package com.example.telegram.to.kafka.app.init;

import com.example.kafka.admin.config.client.KafkaAdminClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaInitializerImpl implements KafkaInitializer {

    private final KafkaAdminClient kafkaAdminClient;

    @Override
    public void init() {
        kafkaAdminClient.createTopic();
        kafkaAdminClient.checkTopicsCreated();
        kafkaAdminClient.checkSchemaRegistryAvailableWithRetry();
    }

}
