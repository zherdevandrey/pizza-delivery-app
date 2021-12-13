package com.example.analytic.data.impl;

import com.example.analytic.data.AnalyticCustomRepository;
import com.example.analytic.data.entity.AnalyticEntity;
import com.example.analytic.mapper.AnalyticDataMapper;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class AnalyticCustomRepositoryImpl implements AnalyticCustomRepository {

    private final EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    private int batchSize;

    @Transactional
    @Override
    public List<AnalyticEntity> batchInsert(List<TwitterAnalyticsAvroModel> list) {
        List<AnalyticEntity> analyticEntityList = list.stream()
                .map(AnalyticDataMapper::map)
                .collect(Collectors.toList());

        int batchCount = 0;
        for (int i = 0; i < analyticEntityList.size(); i++) {
            entityManager.persist(analyticEntityList.get(i));
            if (batchCount % batchSize == 0){
                entityManager.flush();
                entityManager.clear();
            }
            batchCount++;
        }
        entityManager.flush();
        entityManager.clear();
        return analyticEntityList;
    }

}
