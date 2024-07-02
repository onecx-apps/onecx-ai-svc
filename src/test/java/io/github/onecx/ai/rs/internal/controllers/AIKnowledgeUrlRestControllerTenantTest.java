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
@TestHTTPEndpoint(AIKnowledgeUrlRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeUrlRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIKnowledgeUrlTest() {
        var aiKnowledgeUrlDto = new CreateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("custom-url");
        aiKnowledgeUrlDto.setDescription("custom-description");
        aiKnowledgeUrlDto.setName("custom-name");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .body(aiKnowledgeUrlDto)
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(dto).isNotNull()
                .returns(aiKnowledgeUrlDto.getName(), from(AIKnowledgeUrlDTO::getName))
                .returns(aiKnowledgeUrlDto.getUrl(), from(AIKnowledgeUrlDTO::getUrl))
                .returns(aiKnowledgeUrlDto.getDescription(), from(AIKnowledgeUrlDTO::getDescription));

        // create ai-knowledge-url without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail())
                .isEqualTo("createKnowledgeUrl.createAIKnowledgeUrlRequestDTO: must not be null");
    }
}
