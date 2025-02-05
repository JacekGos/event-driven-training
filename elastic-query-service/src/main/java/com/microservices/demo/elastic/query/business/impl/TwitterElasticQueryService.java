package com.microservices.demo.elastic.query.business.impl;

import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.business.ElasticQueryService;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.transformer.ElasticToResponseModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.List;


@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    private final WebClient webClient;



    public TwitterElasticQueryService(ElasticToResponseModelTransformer elasticToResponseModelTransformer, ElasticQueryClient<TwitterIndexModel> elasticQueryClient, WebClient webClient) {
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
        this.elasticQueryClient = elasticQueryClient;
        this.webClient = webClient;
    }


    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        LOG.info("Querying elasticsearch by id {}", id);
        return elasticToResponseModelTransformer.getResponseModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        LOG.info("Querying elasticsearch by text {}", text);




        return elasticToResponseModelTransformer.getResponseModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        LOG.info("Querying all documents in elasticsearch");
        return elasticToResponseModelTransformer.getResponseModels(elasticQueryClient.getAllIndexModels());
    }


//    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);
//
//    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;
//
//    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;
//
//    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;
//
//    private final WebClient.Builder webClientBuilder;
//
//    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler assembler,
//                                      ElasticQueryClient<TwitterIndexModel> queryClient,
//                                      ElasticQueryServiceConfigData queryServiceConfigData,
//                                      @Qualifier("webClientBuilder")
//                                              WebClient.Builder clientBuilder) {
//        this.elasticQueryServiceResponseModelAssembler = assembler;
//        this.elasticQueryClient = queryClient;
//        this.elasticQueryServiceConfigData = queryServiceConfigData;
//        this.webClientBuilder = clientBuilder;
//    }
//
//    @Override
//    public ElasticQueryServiceResponseModel getDocumentById(String id) {
//        LOG.info("Querying elasticsearch by id {}", id);
//        return elasticQueryServiceResponseModelAssembler.toModel(elasticQueryClient.getIndexModelById(id));
//    }
//
//    @Override
//    public ElasticQueryServiceAnalyticsResponseModel getDocumentByText(String text, String accessToken) {
//        LOG.info("Querying elasticsearch by text {}", text);
//        List<ElasticQueryServiceResponseModel> elasticQueryServiceResponseModels =
//                elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getIndexModelByText(text));
//        return ElasticQueryServiceAnalyticsResponseModel.builder()
//                .queryResponseModels(elasticQueryServiceResponseModels)
//                .wordCount(getWordCount(text, accessToken))
//                .build();
//    }
//
//    @Override
//    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
//        LOG.info("Querying all documents in elasticsearch");
//        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getAllIndexModels());
//    }
//
//    private Long getWordCount(String text, String accessToken) {
//        if (QueryType.KAFKA_STATE_STORE.getType().equals(elasticQueryServiceConfigData.getWebClient().getQueryType())) {
//            return getFromKafkaStateStore(text, accessToken).getWordCount();
//        } else if (QueryType.ANALYTICS_DATABASE.getType().
//                equals(elasticQueryServiceConfigData.getWebClient().getQueryType())) {
//            return getFromAnayticsDatabase(text, accessToken).getWordCount();
//        }
//        return 0L;
//    }
//
//    private ElasticQueryServiceWordCountResponseModel getFromAnayticsDatabase(String text, String accessToken) {
//        ElasticQueryServiceConfigData.Query queryFromAnalyticsDatabase =
//                elasticQueryServiceConfigData.getQueryFromAnalyticsDatabase();
//        return retrieveResponseModel(text, accessToken, queryFromAnalyticsDatabase);
//    }
//
//    private ElasticQueryServiceWordCountResponseModel getFromKafkaStateStore(String text, String accessToken) {
//        ElasticQueryServiceConfigData.Query queryFromKafkaStateStore =
//                elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
//        return retrieveResponseModel(text, accessToken, queryFromKafkaStateStore);
//    }
//
//    private ElasticQueryServiceWordCountResponseModel retrieveResponseModel(String text,
//                                                                            String accessToken,
//                                                                            ElasticQueryServiceConfigData.Query query) {
//        return webClientBuilder
//                .build()
//                .method(HttpMethod.valueOf(query.getMethod()))
//                .uri(query.getUri(), uriBuilder -> uriBuilder.build(text))
//                .headers(h -> {
//                    h.setBearerAuth(accessToken);
//                    h.set(CORRELATION_ID_HEADER, MDC.get(CORRELATION_ID_KEY));
//                })
//                .accept(MediaType.valueOf(query.getAccept()))
//                .retrieve()
//                .onStatus(
//                        s -> s.equals(HttpStatus.UNAUTHORIZED),
//                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
//                .onStatus(
//                        s -> s.equals(HttpStatus.BAD_REQUEST),
//                        clientResponse -> Mono.just(new
//                                ElasticQueryServiceException(clientResponse.statusCode().toString())))
//                .onStatus(
//                        s -> s.equals(HttpStatus.INTERNAL_SERVER_ERROR),
//                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString())))
//                .bodyToMono(ElasticQueryServiceWordCountResponseModel.class)
//                .log()
//                .block();
//
//    }
}
