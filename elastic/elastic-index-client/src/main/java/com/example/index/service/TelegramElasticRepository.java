package com.example.index.service;

import com.example.elastic.model.impl.TelegramIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TelegramElasticRepository extends ElasticsearchRepository<TelegramIndexModel, String> {
}
