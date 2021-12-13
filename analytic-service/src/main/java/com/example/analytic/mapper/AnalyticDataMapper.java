package com.example.analytic.mapper;

import com.example.analytic.data.entity.AnalyticEntity;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

public final class AnalyticDataMapper {

    private AnalyticDataMapper() {
    }

    public static AnalyticEntity map(TwitterAnalyticsAvroModel twitterAvroModel) {
        return AnalyticEntity.builder()
                .id(UUID.randomUUID())
                .count(twitterAvroModel.getWordCount())
                .word(twitterAvroModel.getWord())
                .createdAt(mapLocalDataTime(twitterAvroModel.getCreatedAt()))
                .build();
    }

    private static LocalDateTime mapLocalDataTime(Long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault().toZoneId());
    }
}
