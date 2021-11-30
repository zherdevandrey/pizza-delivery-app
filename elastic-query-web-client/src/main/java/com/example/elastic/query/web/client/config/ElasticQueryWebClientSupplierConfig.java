package com.example.elastic.query.web.client.config;

import com.example.app.config.data.ElasticQueryWebClientConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Primary
public class ElasticQueryWebClientSupplierConfig implements ServiceInstanceListSupplier {

    private final ElasticQueryWebClientConfigData webClientConfig;

    @Override
    public String getServiceId() {
        return webClientConfig.getWebclient().getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(
                webClientConfig.getWebclient().getInstances().stream()
                        .map(instance ->
                                new DefaultServiceInstance(
                                        instance.getId(),
                                        getServiceId(),
                                        instance.getHost(),
                                        instance.getPort(),
                                        false
                                )).collect(Collectors.toList()));
    }

}
