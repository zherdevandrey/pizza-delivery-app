package com.example.index.service;

import com.example.elastic.model.impl.TelegramIndexModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticRepositoryIndexClientImpl implements ElasticIndexClient<TelegramIndexModel> {

    private final TelegramElasticRepository repository;

    @Override
    public List<String> save(List<TelegramIndexModel> indexModels) {
        return StreamSupport
                .stream(repository.saveAll(indexModels).spliterator(), false)
                .map(TelegramIndexModel::getId)
                .collect(Collectors.toList());
    }

}
