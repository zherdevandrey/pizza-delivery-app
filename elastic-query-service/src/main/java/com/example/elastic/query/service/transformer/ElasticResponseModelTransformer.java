package com.example.elastic.query.service.transformer;

import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticResponseModelTransformer {

    public ElasticQueryServiceResponseModel transform(TelegramIndexModel telegramIndexModel){
        return ElasticQueryServiceResponseModel
                .builder()
                .id(telegramIndexModel.getId())
                .text(telegramIndexModel.getText())
                .createdAt(telegramIndexModel.getCreatedAt())
                .build();
    }

    public List<ElasticQueryServiceResponseModel> transform(List<TelegramIndexModel> telegramIndexModels){
        return telegramIndexModels
                .stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

}
