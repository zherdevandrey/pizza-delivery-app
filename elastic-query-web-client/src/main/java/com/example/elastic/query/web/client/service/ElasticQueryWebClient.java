package com.example.elastic.query.web.client.service;

import com.example.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;

import java.util.*;

public interface ElasticQueryWebClient {

    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
