package com.example.index.service;

import com.example.app.config.data.ElasticConfigData;
import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.index.client.ElasticIndexUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
@RequiredArgsConstructor
public class ElasticIndexClientImpl implements ElasticIndexClient<TelegramIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticIndexUtil<TelegramIndexModel> elasticIndexUtil;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<String> save(List<TelegramIndexModel> indexModels) {
        List<IndexQuery> queries = elasticIndexUtil.getIndexQueries(indexModels);
        return elasticsearchOperations.bulkIndex(
                queries,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        )
                .stream()
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());
    }

}
