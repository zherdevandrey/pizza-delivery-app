package com.example.elastic.query.client;

import com.example.elastic.model.IndexModel;

import java.util.List;


public interface ElasticQueryClient<T extends IndexModel> {

    T getIndexModelById(String id);

    List<T> getIndexModelByText(String text);

    List<T> getAllIndexModels();

}
