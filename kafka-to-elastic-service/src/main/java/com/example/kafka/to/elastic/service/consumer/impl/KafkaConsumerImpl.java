package com.example.kafka.to.elastic.service.consumer.impl;

import com.example.app.config.data.KafkaConfigData;
import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.index.service.ElasticIndexClient;
import com.example.kafka.admin.config.client.KafkaAdminClient;
import com.example.kafka.avro.model.TelegramAvroModel;
import com.example.kafka.to.elastic.service.consumer.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer<Long, TelegramAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;
    private final ElasticIndexClient elasticIndexClient;

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("TelegramTopicListener").start();
    }

    @Override
    @KafkaListener(id = "TelegramTopicListener", topics = "telegram-topic")
    public void receive(@Payload List<TelegramAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<TelegramIndexModel> telegramIndexModels = messages
                .stream()
                .map(this::mapTelegramModel)
                .collect(Collectors.toList());

        elasticIndexClient.save(telegramIndexModels);
    }

    private TelegramIndexModel mapTelegramModel(TelegramAvroModel TelegramAvroModel) {

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(TelegramAvroModel.getCreatedAt()),
                ZoneId.systemDefault());

        return TelegramIndexModel
                .builder()
                .userId(TelegramAvroModel.getUserId())
                .id(String.valueOf(TelegramAvroModel.getId()))
                .text(TelegramAvroModel.getText())
                .build();

    }
}
