package com.example.elastic.query.client;

import com.example.common.CollectionsUtil;
import com.example.elastic.model.impl.TelegramIndexModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class TelegramElasticRepositoryQueryClient implements ElasticQueryClient<TelegramIndexModel> {

    private final TelegramElasticsearchQueryRepository telegramElasticsearchQueryRepository;

    @Override
    public TelegramIndexModel getIndexModelById(String id) {
        Optional<TelegramIndexModel> searchResult = telegramElasticsearchQueryRepository.findById(id);
        log.info("Document with id {} retrieved successfully",
                searchResult.orElseThrow(() ->
                        new ElasticQueryClientException("No document found at elasticsearch with id " + id)).getId());
        return searchResult.get();
    }

    @Override
    public List<TelegramIndexModel> getIndexModelByText(String text) {
        List<TelegramIndexModel> searchResult = telegramElasticsearchQueryRepository.findByText(text);
        log.info("{} of documents with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TelegramIndexModel> getAllIndexModels() {
        List<TelegramIndexModel> searchResult =
                CollectionsUtil.getInstance().getListFromIterable(telegramElasticsearchQueryRepository.findAll());
        log.info("{} number of documents retrieved successfully", searchResult.size());
        return searchResult;
    }
}
