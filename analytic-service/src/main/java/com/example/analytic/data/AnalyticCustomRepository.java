package com.example.analytic.data;


import com.example.analytic.data.entity.AnalyticEntity;
import com.example.kafka.avro.model.TelegramAnalyticsAvroModel;

import java.util.List;

public interface AnalyticCustomRepository {

    List<AnalyticEntity> batchInsert(List<TelegramAnalyticsAvroModel> list);

}
