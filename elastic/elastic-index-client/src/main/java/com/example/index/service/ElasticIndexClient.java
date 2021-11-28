package com.example.index.service;

import com.example.elastic.model.impl.TelegramIndexModel;

import java.util.List;

public interface ElasticIndexClient<T> {
    List<String> save(List<TelegramIndexModel> indexModels);
}
