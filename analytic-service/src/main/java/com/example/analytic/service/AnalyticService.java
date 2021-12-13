package com.example.analytic.service;

import com.example.analytic.data.AnalyticDataRepository;
import com.example.analytic.data.entity.AnalyticEntity;
import com.example.analytic.model.AnalyticsResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final AnalyticDataRepository repository;

    @Transactional
    public Optional<AnalyticsResponseModel> getAnalyticData(String word) {
        return repository
                .findByWord(word)
                .findFirst()
                .map(this::map);
    }

    private AnalyticsResponseModel map(AnalyticEntity analyticEntity) {
        return AnalyticsResponseModel.builder()
                .id(analyticEntity.getId())
                .word(analyticEntity.getWord())
                .wordCount(analyticEntity.getCount())
                .build();
    }
}
