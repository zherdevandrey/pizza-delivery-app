package com.example.elastic.query.client;

import com.example.elastic.model.impl.TelegramIndexModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramElasticsearchQueryRepository extends ElasticsearchRepository<TelegramIndexModel, String> {

    List<TelegramIndexModel> findByText(String text);
}
