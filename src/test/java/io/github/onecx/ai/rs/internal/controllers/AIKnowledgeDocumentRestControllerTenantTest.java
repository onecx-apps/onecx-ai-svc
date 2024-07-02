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
@TestHTTPEndpoint(AIKnowledgeDocumentRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeDocumentRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIKnowledgeDocumentTest() {
        var aiKnowledgeDocumentDto = new CreateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("document-ref-id-test");
        aiKnowledgeDocumentDto.setName("document-ref-name-test");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.NEW);

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .body(aiKnowledgeDocumentDto)
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(dto).isNotNull()
                .returns(aiKnowledgeDocumentDto.getName(), from(AIKnowledgeDocumentDTO::getName))
                .returns(aiKnowledgeDocumentDto.getDocumentRefId(), from(AIKnowledgeDocumentDTO::getDocumentRefId))
                .returns(aiKnowledgeDocumentDto.getStatus(), from(AIKnowledgeDocumentDTO::getStatus));

        // create ai-knowledge-document without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-context-11-111")
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail())
                .isEqualTo("createKnowledgeDocument.createAIKnowledgeDocumentRequestDTO: must not be null");
    }
}
