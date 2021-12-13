package com.example.analytic.data;


import com.example.analytic.data.entity.AnalyticEntity;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;

import java.util.*;

public interface AnalyticCustomRepository {

    List<AnalyticEntity> batchInsert(List<TwitterAnalyticsAvroModel> list);

}
