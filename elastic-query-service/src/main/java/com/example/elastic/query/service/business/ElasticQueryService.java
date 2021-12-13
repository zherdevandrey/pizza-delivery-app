package com.example.elastic.query.service.business;

import com.example.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.example.elastic.query.service.model.ElasticQueryServiceResponseModel;

import java.util.*;


public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);

    ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(String text);

    List<ElasticQueryServiceResponseModel> getAllDocuments();

}
