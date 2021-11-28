package com.example.elastic.query.service.business;

import com.example.app.config.data.ElasticQueryConfigData;
import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.elastic.query.client.ElasticQueryClient;
import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.example.elastic.query.service.transformer.ElasticResponseModelTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramElasticQueryService implements ElasticQueryService {

    private final ElasticResponseModelTransformer transformer;
    private final ElasticQueryClient<TelegramIndexModel> elasticQueryClient;
    private final ElasticQueryConfigData elasticQueryConfigData;


    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        TelegramIndexModel telegramIndexModel = elasticQueryClient.getIndexModelById(id);
        return transformer.transform(telegramIndexModel);
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        return elasticQueryClient
                .getIndexModelByText(text)
                .stream()
                .map(transformer::transform)
                .collect(Collectors.toList());
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        return elasticQueryClient
                .getAllIndexModels()
                .stream()
                .map(transformer::transform)
                .collect(Collectors.toList());
    }

}