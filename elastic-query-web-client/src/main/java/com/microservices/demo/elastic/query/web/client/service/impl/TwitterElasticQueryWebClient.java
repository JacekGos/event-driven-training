package com.microservices.demo.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.List;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient.Builder webClientBuilder;

    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder clientBuilder,
                                        ElasticQueryWebClientConfigData webClientConfigData) {
        this.webClientBuilder = clientBuilder;
        this.elasticQueryWebClientConfigData = webClientConfigData;
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        LOG.info("Querying by text {}", requestModel.getText());

        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryWebClientResponseModel.class)
                .collectList()
                .block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
//                .header(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_KEY))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
                .onStatus(
                        s -> s.equals(HttpStatus.BAD_REQUEST),
                        clientResponse -> Mono.just(
                                new ElasticQueryWebClientException(clientResponse.statusCode().toString())))
                .onStatus(
                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }

    //    @Override
//    public ElasticQueryWebClientAnalyticsResponseModel getDataByText(ElasticQueryWebClientRequestModel requestModel) {
//        LOG.info("Querying by text {}", requestModel.getText());
//        return getWebClient(requestModel)
//                .bodyToMono(ElasticQueryWebClientAnalyticsResponseModel.class)
//                .log()//Logger
//                .block();
//    }
//
//    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
//        return webClientBuilder
//                .build()
//                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
//                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
//                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
//                .header(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_KEY))
//                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
//                .retrieve()
//                .onStatus(
//                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
//                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
//                .onStatus(
//                        s -> s.equals(HttpStatus.BAD_REQUEST),
//                        clientResponse -> Mono.just(
//                                new ElasticQueryWebClientException(clientResponse.statusCode().toString())))
//                .onStatus(
//                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
//                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString())));
//    }
//
//

}
