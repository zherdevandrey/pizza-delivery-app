package com.example.elastic.query.service.business;

import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;

import java.util.*;


public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);

    List<ElasticQueryServiceResponseModel> getDocumentsByText(String text);

    List<ElasticQueryServiceResponseModel> getAllDocuments();

}
