package com.example.kafka.admin.config.client;

import com.example.app.config.data.KafkaConfigData;
import com.example.app.config.data.RetryConfigData;
import com.example.kafka.admin.config.exception.KafkaClientException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAdminClient {

    private final RetryConfigData retryConfigData;
    private final RetryTemplate retryTemplate;
    private final AdminClient adminClient;
    private final KafkaConfigData kafkaConfigData;
    private final WebClient webClient;

    public void createTopic() {
        retryTemplate.execute(retryContext -> {
            CreateTopicsResult result;
            try {
                result = doCreateTopics(retryContext);
            } catch (Exception ex) {
                throw new KafkaClientException("Cannot create topics", ex);
            }
            return result;
        });
        checkTopicsCreated();
        checkSchemaRegistryAvailableWithRetry();
    }

    public CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        kafkaConfigData
                .getTopicNamesToCreate()
                .forEach(topic -> {
                    log.debug("Creating topic with name {} attempts #{}", topic, retryContext.getRetryCount());
                });
        List<NewTopic> kafkaTopics = kafkaConfigData
                .getTopicNamesToCreate()
                .stream()
                .map(topic -> new NewTopic(topic, kafkaConfigData.getNumOfPartitions(), kafkaConfigData.getReplicationFactor()))
                .collect(Collectors.toList());

        return adminClient.createTopics(kafkaTopics);
    }

    @SneakyThrows
    public void checkSchemaRegistryAvailableWithRetry(){
        retryTemplate.execute(retryContext -> {
            try {
                return checkSchemaRegistryAvailable();
            }catch (Exception ex){
                log.error("Schema registry is not available");
                throw new KafkaClientException("Schema registry is not available", ex);
            }
        });
    }

    private HttpStatus checkSchemaRegistryAvailable() {
        HttpStatus status = webClient
                .method(HttpMethod.GET)
                .uri(kafkaConfigData.getSchemaRegistryUrl())
                .exchange()
                .map(ClientResponse::statusCode)
                .block();

        if(status != HttpStatus.OK){
            throw new KafkaClientException("Schema registry is not available");
        }
        return status;
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    private Collection<TopicListing> getTopics() {
        try {
            return retryTemplate.execute(this::doGetTopics);
        } catch (Exception ex) {
            throw new KafkaClientException("Cannot get topics", ex);
        }
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        topics.forEach(topic -> log.debug("Topic with name {} created", topic));
        return topics;
    }

    @SneakyThrows
    private void sleep(Long sleepTimeMs) {
        Thread.sleep(sleepTimeMs);
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if (retry > maxRetry) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        List<String> topicNames = topics
                .stream()
                .map(TopicListing::name)
                .collect(Collectors.toList());
        return topicNames.contains(topic);
    }

}
