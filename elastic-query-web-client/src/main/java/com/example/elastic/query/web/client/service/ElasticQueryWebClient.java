package com.example.elastic.query.web.client.service;

import com.example.elastic.query.web.client.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;

import java.util.*;

public interface ElasticQueryWebClient {

    ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
