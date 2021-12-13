package com.example.kafka.streaming.service.runner;

import com.example.app.config.data.KafkaConfigData;
import com.example.app.config.data.KafkaStreamsConfigData;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;
import com.example.kafka.avro.model.TwitterAvroModel;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
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

        KStream<Long, TwitterAvroModel> twitterAvroModelKStream = getTwitterAvroModelKStream(serdeConfig, streamsBuilder);

        createTopology(twitterAvroModelKStream, serdeConfig);

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

    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelKStream,
                                Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Serde<TwitterAnalyticsAvroModel> serdeAnalyticsModel = getSerdeAnalyticsModel(serdeConfig);

        twitterAvroModelKStream
                .flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key, value) -> value)
                .count(Materialized.as(kafkaStreamsConfigData.getWordCountStoreName()))
                .toStream()
                .peek((key, value) -> log.debug("Word {} - count {}", key, value))
                .map(mapToAnalyticsModel())
                .to(kafkaStreamsConfigData.getOutputTopicName(),
                        Produced.with(Serdes.String(), serdeAnalyticsModel));

    }

    private KeyValueMapper<String, Long, KeyValue<? extends String, ? extends TwitterAnalyticsAvroModel>>
    mapToAnalyticsModel() {
        return (word, count) -> {
            log.info("Sending to topic {}, word {} - count {}",
                    kafkaStreamsConfigData.getOutputTopicName(), word, count);
            return new KeyValue<>(word, TwitterAnalyticsAvroModel
                    .newBuilder()
                    .setWord(word)
                    .setWordCount(count)
                    .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build());
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TwitterAnalyticsAvroModel> twitterAnalyticsAvroModel = new SpecificAvroSerde<>();
        twitterAnalyticsAvroModel.configure(serdeConfig, false);
        return twitterAnalyticsAvroModel;
    }

    private Serde<TwitterAvroModel> getSerdeModel(Map<String, String> serdeConfig) {
        Serde<TwitterAvroModel> twitterAvroModel = new SpecificAvroSerde<>();
        twitterAvroModel.configure(serdeConfig, false);
        return twitterAvroModel;
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvroModelKStream(Map<String, String> serdeConfig, StreamsBuilder streamsBuilder) {
        Serde<TwitterAvroModel> serdeTwitterAvroModel = getSerdeModel(serdeConfig);
        return streamsBuilder
                .stream(kafkaStreamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(), serdeTwitterAvroModel));
    }

}
