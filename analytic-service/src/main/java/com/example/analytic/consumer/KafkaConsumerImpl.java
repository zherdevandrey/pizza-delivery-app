package com.example.analytic.consumer;

import com.example.analytic.data.AnalyticDataRepository;
import com.example.analytic.data.entity.AnalyticEntity;
import com.example.analytic.mapper.AnalyticDataMapper;
import com.example.app.config.data.KafkaConfigData;
import com.example.kafka.admin.config.client.KafkaAdminClient;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerImpl implements KafkaConsumer<String, TwitterAnalyticsAvroModel>{

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient adminClient;
    private final KafkaConfigData kafkaConfigData;
    private final AnalyticDataRepository analyticDataRepository;

    @EventListener
    public void onStarted(ApplicationStartedEvent event){
        adminClient.checkTopicsCreated();
        log.info("Required topics created: {}", kafkaConfigData.getTopicNamesToCreate());
        kafkaListenerEndpointRegistry.getListenerContainer("kafkaAnalyticListener").start();
    }

    @KafkaListener(id = "kafkaAnalyticListener", topics = "telegram-analytics-topic")
    @Override
    public void receive(@Payload List<TwitterAnalyticsAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        List<AnalyticEntity> analyticEntityList = messages
                .stream()
                .map(AnalyticDataMapper::map)
                .collect(Collectors.toList());

        analyticDataRepository.saveAll(analyticEntityList);
    }

}
