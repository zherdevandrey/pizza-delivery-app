package com.example.kafka.to.elastic.service.consumer.impl;

import com.example.app.config.data.KafkaConfigData;
import com.example.elastic.model.impl.TelegramModel;
import com.example.kafka.admin.config.client.KafkaAdminClient;
import com.example.kafka.avro.model.TwitterAvroModel;
import com.example.kafka.to.elastic.service.consumer.KafkaConsumer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer<Long, TwitterAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaConfigData kafkaConfigData;

    @Override
    @KafkaListener(id = "telegramTopicListener", topics = "telegram-topic")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offset) {

        log.debug("# {} of messages, keys {}, partitions {}, offset {} thread {}",
                messages.size(),
                keys.size(),
                partitions.toString(),
                offset.toString(),
                Thread.currentThread()
                );

        List<TelegramModel> telegramModels = messages
                .stream()
                .map(this::mapTelegramModel)
                .collect(Collectors.toList());

    }

    private TelegramModel mapTelegramModel(TwitterAvroModel twitterAvroModel){
        return TelegramModel
                .builder()
                .build();
    }
}
