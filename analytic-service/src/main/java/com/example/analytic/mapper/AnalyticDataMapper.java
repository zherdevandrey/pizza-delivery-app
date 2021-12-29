package com.example.analytic.mapper;

import com.example.analytic.data.entity.AnalyticEntity;
import com.example.kafka.avro.model.TelegramAnalyticsAvroModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

public final class AnalyticDataMapper {

    private AnalyticDataMapper() {
    }

    public static AnalyticEntity map(TelegramAnalyticsAvroModel TelegramAvroModel) {
        return AnalyticEntity.builder()
                .id(UUID.randomUUID())
                .count(TelegramAvroModel.getWordCount())
                .word(TelegramAvroModel.getWord())
                .createdAt(mapLocalDataTime(TelegramAvroModel.getCreatedAt()))
                .build();
    }

    private static LocalDateTime mapLocalDataTime(Long timeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault().toZoneId());
    }
}
