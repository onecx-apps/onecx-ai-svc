package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
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
@TestHTTPEndpoint(AIKnowledgeDatabaseRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeDatabaseRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIKnowledgeDatabaseTest() {
        var aiKnowledgeDatabaseDto = new CreateAIKnowledgeDatabaseRequestDTO();
        aiKnowledgeDatabaseDto.setDescription("db-description");
        aiKnowledgeDatabaseDto.setName("db-name");
        aiKnowledgeDatabaseDto.setDb("db-1");
        aiKnowledgeDatabaseDto.setUser("db-user");
        aiKnowledgeDatabaseDto.setPwd("pdw-db");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .body(aiKnowledgeDatabaseDto)
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(dto).isNotNull()
                .returns(aiKnowledgeDatabaseDto.getName(), from(AIKnowledgeDatabaseDTO::getName))
                .returns(aiKnowledgeDatabaseDto.getDescription(), from(AIKnowledgeDatabaseDTO::getDescription))
                .returns(aiKnowledgeDatabaseDto.getDb(), from(AIKnowledgeDatabaseDTO::getDb))
                .returns(aiKnowledgeDatabaseDto.getUser(), from(AIKnowledgeDatabaseDTO::getUser));

        // create ai-knowledge-database without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail())
                .isEqualTo("createKnowledgeDb.createAIKnowledgeDatabaseRequestDTO: must not be null");
    }
}
