package com.example.kafka.streaming.service.runner;

import com.example.app.config.data.KafkaConfigData;
import com.example.app.config.data.KafkaStreamsConfigData;
import com.example.kafka.avro.model.TelegramAnalyticsAvroModel;
import com.example.kafka.avro.model.TelegramAvroModel;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStreamsRunner {

    private static final String REGEX = "\\W+";

    private final KafkaConfigData kafkaConfigData;
    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;
    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;

    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl());

        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<Long, TelegramAvroModel> TelegramAvroModelKStream = getTelegramAvroModelKStream(serdeConfig, streamsBuilder);

        createTopology(TelegramAvroModelKStream, serdeConfig);

        startStreaming(streamsBuilder);
    }

    public Long getValueByKey(String word) {
        if (kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING) {
            if (keyValueStore == null) {
                synchronized (this) {
                    if (keyValueStore == null) {
                        keyValueStore = kafkaStreams.store(StoreQueryParameters
                                .fromNameAndType(kafkaStreamsConfigData.getWordCountStoreName(),
                                        QueryableStoreTypes.keyValueStore()));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    private void startStreaming(StreamsBuilder streamsBuilder) {
        Topology topology = streamsBuilder.build();
        log.info("Defined topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        log.info("Kafka streaming started..");
    }

    private void createTopology(KStream<Long, TelegramAvroModel> TelegramAvroModelKStream,
                                Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Serde<TelegramAnalyticsAvroModel> serdeAnalyticsModel = getSerdeAnalyticsModel(serdeConfig);

        TelegramAvroModelKStream
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, value) -> value)
                .count(Materialized.as(kafkaStreamsConfigData.getWordCountStoreName()))
                .toStream()
                .peek((key, value) -> log.debug("Word {} - count {}", key, value))
                .map(mapToAnalyticsModel())
                .to(kafkaStreamsConfigData.getOutputTopicName(),
                        Produced.with(Serdes.String(), serdeAnalyticsModel));

    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TelegramAnalyticsAvroModel>>
    mapToAnalyticsModel() {
        return (word, count) -> {
            log.info("Sending to topic {}, word {} - count {}",
                    kafkaStreamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, TelegramAnalyticsAvroModel
                    .newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<TelegramAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TelegramAnalyticsAvroModel> TelegramAnalyticsAvroModel = new SpecificAvroSerde<>();
        TelegramAnalyticsAvroModel.configure(serdeConfig, false);
        return TelegramAnalyticsAvroModel;
    }

    private Serde<TelegramAvroModel> getSerdeModel(Map<String, String> serdeConfig) {
        Serde<TelegramAvroModel> TelegramAvroModel = new SpecificAvroSerde<>();
        TelegramAvroModel.configure(serdeConfig, false);
        return TelegramAvroModel;
    }

    private KStream<Long, TelegramAvroModel> getTelegramAvroModelKStream(Map<String, String> serdeConfig, StreamsBuilder streamsBuilder) {
        Serde<TelegramAvroModel> serdeTelegramAvroModel = getSerdeModel(serdeConfig);
        return streamsBuilder
                .stream(kafkaStreamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(), serdeTelegramAvroModel));
    }

}
