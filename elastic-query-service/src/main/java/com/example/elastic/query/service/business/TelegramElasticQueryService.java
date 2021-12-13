package com.example.elastic.query.service.business;

import com.example.app.config.data.ElasticQueryServiceConfigData;
import com.example.elastic.model.impl.TelegramIndexModel;
import com.example.elastic.query.client.ElasticQueryClient;
import com.example.elastic.query.service.QueryType;
import com.example.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;
import com.example.elastic.query.service.model.ElasticQueryServiceWordCountResponseModel;
import com.example.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.elastic.query.service.QueryType.ANALYTICS_DATABASE;
import static com.example.elastic.query.service.QueryType.KAFKA_STATE_STORE;

@Service
@RequiredArgsConstructor
public class TelegramElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseModelAssembler assembler;
    private final ElasticQueryClient<TelegramIndexModel> elasticQueryClient;
    private final WebClient.Builder webClient;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    @Value("${elastic-query-service.webclient.query-type}")
    private String queryType;

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        TelegramIndexModel telegramIndexModel = elasticQueryClient.getIndexModelById(id);
        return assembler.toModel(telegramIndexModel);
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(String text) {
        List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels = elasticQueryClient
                .getIndexModelByText(text)
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ElasticQueryServiceAnalyticsResponseModel
                .builder()
                .queryResponseModels(elasticQueryServiceResponseModels)
                .wordCount(getWordCount(text))
                .build();
    }

    private long getWordCount(String text) {
        if (QueryType.valueOf(queryType) == KAFKA_STATE_STORE){
            return getWordCountFromKafkaStore(text);
        }
        if (QueryType.valueOf(queryType) == ANALYTICS_DATABASE){
            return getWordCountFromDataBaseStore(text);
        }
        throw new IllegalStateException("Query type cannot be defined");
    }

    private long getWordCountFromKafkaStore(String text) {
        ElasticQueryServiceConfigData.Query query = elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
        ElasticQueryServiceWordCountResponseModel wordCountResponseModel = getWordCountFromDataBaseStore(text, query);
        return wordCountResponseModel.getWordCount();
    }

    private long getWordCountFromDataBaseStore(String text) {
        ElasticQueryServiceConfigData.Query query = elasticQueryServiceConfigData.getQueryFromAnalyticsDatabase();
        ElasticQueryServiceWordCountResponseModel wordCountResponseModel = getWordCountFromDataBaseStore(text, query);
        return wordCountResponseModel.getWordCount();
    }

    private ElasticQueryServiceWordCountResponseModel getWordCountFromDataBaseStore(String text, ElasticQueryServiceConfigData.Query query) {
        return webClient
                .build()
                .method(HttpMethod.valueOf(query.getMethod()))
                .uri(query.getUri(), uriBuilder -> uriBuilder.build(text))
                .retrieve()
                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
                .block();
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        return elasticQueryClient
                .getAllIndexModels()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
    }

}
