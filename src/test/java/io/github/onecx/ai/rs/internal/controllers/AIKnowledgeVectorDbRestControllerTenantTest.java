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
@TestHTTPEndpoint(AIKnowledgeVectorDbRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeVectorDbRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIKnowledgeVectorDbTest() {

        var aiKnowledgeVectorDbDto = new CreateAIKnowledgeVectorDbRequestDTO();
        aiKnowledgeVectorDbDto.setName("vector-name");
        aiKnowledgeVectorDbDto.setDescription("vector-description");
        aiKnowledgeVectorDbDto.setPwd("vector-pwd");
        aiKnowledgeVectorDbDto.setUser("user1");
        aiKnowledgeVectorDbDto.setVdbUrl("vdb-url-test");
        aiKnowledgeVectorDbDto.setVdbCollection("collection-test");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .body(aiKnowledgeVectorDbDto)
                .post("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-vdbs/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-vdbs/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(dto).isNotNull()
                .returns(aiKnowledgeVectorDbDto.getName(), from(AIKnowledgeVectorDbDTO::getName))
                .returns(aiKnowledgeVectorDbDto.getVdbCollection(), from(AIKnowledgeVectorDbDTO::getVdbCollection))
                .returns(aiKnowledgeVectorDbDto.getDescription(), from(AIKnowledgeVectorDbDTO::getDescription))
                .returns(aiKnowledgeVectorDbDto.getVdbUrl(), from(AIKnowledgeVectorDbDTO::getVdbUrl));

        // create ai-knowledge-vector-db without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .post("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail())
                .isEqualTo("createKnowledgeVectorDb.createAIKnowledgeVectorDbRequestDTO: must not be null");
    }
}
