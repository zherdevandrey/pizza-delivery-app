package com.example.config;


import com.example.app.config.data.ElasticConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages = "com.example")
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    private final ElasticConfigData configData;

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        UriComponents serverUri = UriComponentsBuilder.fromHttpUrl(configData.getConnectionUrl()).build();

        HttpHost httpHost = new HttpHost(
                serverUri.getHost(),
                serverUri.getPort(),
                serverUri.getScheme()
        );
        RestClientBuilder restClientBuilder = RestClient
                .builder(httpHost)
                .setRequestConfigCallback(builder -> builder
                        .setConnectionRequestTimeout(configData.getConnectionTimeOutMs())
                        .setSocketTimeout(configData.getSocketTimeOutMs()));
        return new RestHighLevelClient(restClientBuilder);
    }

}
