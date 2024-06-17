package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AIProviderRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class AIProviderRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIProviderTest() {

        // create kb
        var providerDto = new CreateAIProviderRequestDTO();
        providerDto.setAppId("appId");
        providerDto.setName("Provider");
        providerDto.setModelName("ModelName");
        providerDto.setModelVersion("ModelVersion");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(providerDto)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIProviderDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIProviderDTO.class);

        assertThat(dto).isNotNull()
                .returns(providerDto.getName(), from(AIProviderDTO::getName))
                .returns(providerDto.getModelName(), from(AIProviderDTO::getModelName))
                .returns(providerDto.getModelVersion(), from(AIProviderDTO::getModelVersion));

        // create provider without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .post()
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createAIProvider.createAIProviderRequestDTO: must not be null");

    }

}
