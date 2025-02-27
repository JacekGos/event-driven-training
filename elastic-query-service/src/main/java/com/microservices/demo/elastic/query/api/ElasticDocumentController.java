package com.microservices.demo.elastic.query.api;


import com.microservices.demo.elastic.query.business.ElasticQueryService;
import com.microservices.demo.elastic.query.model.ElasticQueryServiceResponseModelV2;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);
    private final ElasticQueryService elasticQueryService;

    @Value("${server.port}")
    private String port;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }


    @Operation(summary = "get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
        LOG.info("Elasticsearch getAllDocuments request");
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elasticsearch returned {} of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModel>
    getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        LOG.debug("Elasticsearch returned document with id {} on port {}", id, port);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @Operation(summary = "Get elastic document by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v2+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModelV2>
    getDocumentByIdV2(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        LOG.debug("Elasticsearch returned document with id {} on port {}", id, port);
        ElasticQueryServiceResponseModelV2 response = getV2Model(elasticQueryServiceResponseModel);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @Operation(summary = "Get elastic document by text.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
                    @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @PostMapping("/get-document-by-text")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>>
    getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {
        LOG.info("Querying documents for text {}",
                elasticQueryServiceRequestModel.getText());

        List<ElasticQueryServiceResponseModel> response =
                elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText());

        LOG.info("Elasticsearch returned {} of documents on port: {}", response.size(), port);

        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel) {
        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
                .id(Long.parseLong(responseModel.getId()))
                .userId(responseModel.getUserId())
                .text(responseModel.getText())
                .text2("Version 2 text")
                .build();
        responseModelV2.add(responseModel.getLinks());
        return responseModelV2;

    }

//    @PostMapping("/get-document-by-text")
//    public @ResponseBody
//    ResponseEntity<List<ElasticQueryServiceResponseModel>>
//    getDocumentByText(@RequestBody ElasticQueryServiceRequestModel elasticQueryServiceRequestModel) {
//        List<ElasticQueryServiceResponseModel> response = new ArrayList<>();
//        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel =
//                ElasticQueryServiceResponseModel
//                        .builder()
//                        .text(elasticQueryServiceRequestModel.getText())
//                        .build();
//        response.add(elasticQueryServiceResponseModel);
//
//        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
//
//        LOG.info("Elasticsearch returned {} of documents", response.size());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "Get elastic document by id.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
//                    @Content(mediaType = "application/vnd.api.v2+json",
//                            schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
//                    )
//            }),
//            @ApiResponse(responseCode = "400", description = "Not found."),
//            @ApiResponse(responseCode = "500", description = "Internal server error.")
//    })
//    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
//    public @ResponseBody
//    ResponseEntity<ElasticQueryServiceResponseModelV2>
//    getDocumentByIdV2(@PathVariable @NotEmpty String id) {
//        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
//        ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(elasticQueryServiceResponseModel);
//        LOG.debug("Elasticsearch returned document with id {} on port {}", id, port);
//        return ResponseEntity.ok(responseModelV2);
//    }
//
//
//    @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
//    @PostAuthorize("hasPermission(returnObject, 'READ')")
//    @Operation(summary = "Get elastic document by text.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successful response.", content = {
//                    @Content(mediaType = "application/vnd.api.v1+json",
//                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
//                    )
//            }),
//            @ApiResponse(responseCode = "400", description = "Not found."),
//            @ApiResponse(responseCode = "500", description = "Internal server error.")
//    })
//    @PostMapping("/get-document-by-text")
//    public @ResponseBody
//    ResponseEntity<ElasticQueryServiceAnalyticsResponseModel>
//    getDocumentByText(@RequestBody @Valid ElasticQueryServiceRequestModel elasticQueryServiceRequestModel,
//                      @AuthenticationPrincipal TwitterQueryUser principal,
//                      @RegisteredOAuth2AuthorizedClient("keycloak")
//                      OAuth2AuthorizedClient oAuth2AuthorizedClient) {
//        LOG.info("User {} querying documents for text {}", principal.getUsername(),
//                elasticQueryServiceRequestModel.getText());
//
//        ElasticQueryServiceAnalyticsResponseModel response =
//                elasticQueryService.getDocumentByText(elasticQueryServiceRequestModel.getText(),
//                        oAuth2AuthorizedClient.getAccessToken().getTokenValue());
//        LOG.info("Elasticsearch returned {} of documents on port {}",
//                response.getQueryResponseModels().size(), port);
//        return ResponseEntity.ok(response);
//    }
//
//    private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel) {
//        ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
//                .id(Long.parseLong(responseModel.getId()))
//                .userId(responseModel.getUserId())
//                .text(responseModel.getText())
//                .text2("Version 2 text")
//                .build();
//        responseModelV2.add(responseModel.getLinks());
//        return responseModelV2;
//
//    }


}
