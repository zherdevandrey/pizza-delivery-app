package com.example.telegram.to.kafka.app.listener;

import com.example.app.config.data.KafkaConfigData;
import com.example.kafka.avro.model.TelegramAvroModel;
import com.example.kafka.producer.service.KafkaProducerImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import twitter4j.Status;

import java.util.Locale;

@Slf4j
@Service
@AllArgsConstructor
public class TelegramKafkaStatusListener {

    private final KafkaProducerImpl kafkaProducer;
    private final KafkaConfigData kafkaConfigData;

    public void onStatus(Status status) {
        TelegramAvroModel model = TelegramAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
        kafkaProducer.produce(kafkaConfigData.getTopicName(), status.getId(), model);
        log.info("PUBLISHED MODEL {} TO {}", model, kafkaConfigData.getTopicName().toUpperCase(Locale.ROOT));
        log.info("================================");
    }

}
