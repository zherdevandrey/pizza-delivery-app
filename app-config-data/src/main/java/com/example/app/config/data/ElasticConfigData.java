package com.example.app.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("elastic-config")
public class ElasticConfigData {

    private String indexName;
    private String connectionUrl;
    private Integer connectionTimeOutMs;
    private Integer socketTimeOutMs;

}
