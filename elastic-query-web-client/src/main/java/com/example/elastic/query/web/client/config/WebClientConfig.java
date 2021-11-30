package com.example.elastic.query.web.client.config;

import com.example.app.config.data.ElasticQueryWebClientConfigData;
import com.example.app.config.data.UserConfigData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@LoadBalancerClient(name = "elastic-query-service", configuration = ElasticQueryWebClientSupplierConfig.class)
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;
    private final UserConfigData userConfigData;

    @LoadBalanced
    @Bean("webClientBuilder")
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(ExchangeFilterFunctions
                        .basicAuthentication(userConfigData.getUsername(), userConfigData.getPassword()))
                .baseUrl(elasticQueryWebClientConfigData.getWebclient().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, elasticQueryWebClientConfigData.getWebclient().getContentType())
                .defaultHeader(HttpHeaders.ACCEPT, elasticQueryWebClientConfigData.getWebclient().getAcceptType())
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(getTcpClient())))
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(elasticQueryWebClientConfigData.getWebclient().getMaxInMemorySize()));
    }

    private TcpClient getTcpClient() {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, elasticQueryWebClientConfigData.getWebclient().getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(elasticQueryWebClientConfigData.getWebclient().getReadTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWebclient().getWriteTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                });
    }

}
