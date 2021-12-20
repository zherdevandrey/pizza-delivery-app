package com.example.kafka.producer.service;

import com.example.kafka.avro.model.TwitterAvroModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer<Long, TwitterAvroModel> {

    private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    @Override
    public void produce(String topicName, Long key, TwitterAvroModel message) {
        kafkaTemplate
                .send(topicName, key, message)
                .addCallback(new ListenableFutureCallback<>() {

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Error while sending message {} to topic {}".toUpperCase(Locale.ROOT), message.toString(), topicName, ex);
                    }

                    @Override
                    public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                        RecordMetadata metadata = result.getRecordMetadata();
                        log.info("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}".toUpperCase(Locale.ROOT),
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset(),
                                metadata.timestamp(),
                                System.nanoTime());
                    }
                });
    }
}
