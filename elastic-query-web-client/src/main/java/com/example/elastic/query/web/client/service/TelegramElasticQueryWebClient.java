package com.example.elastic.query.web.client.service;

import com.example.app.config.data.ElasticQueryWebClientConfigData;
import com.example.elastic.query.web.client.exception.ElasticQueryWebClientException;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientAnalyticsResponseModel;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.example.Constants.CORRELATION_ID_HEADER;
import static com.example.Constants.CORRELATION_ID_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramElasticQueryWebClient implements ElasticQueryWebClient {

    @Qualifier("webClientBuilder")
    private final WebClient.Builder webClientBuilder;
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;


    @Override
    public ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        log.info("Querying by text {}", requestModel.getText());
        ElasticQueryWebClientAnalyticsResponseModel result = getWebClient(requestModel)
                .bodyToMono(ElasticQueryWebClientAnalyticsResponseModel.class)
                .block();
        log.info("Retrieved #{} records", result.getQueryResponseModels().size());
        log.info("Records: {}", result);
        return result;
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .header(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_KEY))
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        cr -> Mono.just(new ElasticQueryWebClientException(cr.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }
}
