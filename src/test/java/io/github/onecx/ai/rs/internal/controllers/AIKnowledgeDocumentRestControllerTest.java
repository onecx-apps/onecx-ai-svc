package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusTest
@TestHTTPEndpoint(AIKnowledgeDocumentRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeDocumentRestControllerTest extends AbstractTest {

    @Test
    void createAIKnowledgeDocumentSuccessfullyTest() {
        var aiKnowledgeDocumentDto = new CreateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("document-ref-id-test");
        aiKnowledgeDocumentDto.setName("document-ref-name-test");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.NEW);

        //get ai-context
        var contextDto = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "context-11-111")
                .get("/ai-contexts/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(contextDto).isNotNull();

        //create knowledge-document
        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", contextDto.getId())
                .body(aiKnowledgeDocumentDto)
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(aiKnowledgeDocumentDto.getName());
        assertThat(response.getDocumentRefId()).isEqualTo(aiKnowledgeDocumentDto.getDocumentRefId());
        assertThat(response.getStatus()).isEqualTo(aiKnowledgeDocumentDto.getStatus());

        //get ai-knowledge-documents by ai-context
        var documents = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", contextDto.getId())
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(documents).isNotNull();
        assertThat(documents).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void createAIKnowledgeDocumentWithoutAIContextThrowsExceptionTest() {
        var aiKnowledgeDocumentDto = new CreateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("document-ref-id-test");
        aiKnowledgeDocumentDto.setName("document-ref-name-test");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.NEW);

        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-ai-context-id")
                .body(aiKnowledgeDocumentDto)
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("AI_CONTEXT_DOES_NOT_EXIST", exception.getErrorCode());
        Assertions.assertEquals("AIContext does not exist", exception.getDetail());
    }

    @Test
    void getAIKnowledgeDocumentsByContextIdTest() {
        //get ai-knowledge-documents by ai-context
        var documents = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "context-22-222")
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(documents).isNotNull();
        assertThat(documents).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void getAIKnowledgeDocumentTest() {
        //get non-existing ai-knowledge-document
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-id")
                .get("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //get existing ai-knowledge-document
        var dto = given()
                .contentType(APPLICATION_JSON)
                .when().pathParam("id", "document-22-222")
                .get("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("document-22-222");
        assertThat(dto.getName()).isEqualTo("knowledge_document_name2");
        assertThat(dto.getDocumentRefId()).isEqualTo("document_ref2");
        assertThat(dto.getStatus()).isEqualTo(DocumentStatusTypeDTO.PROCESSING);
    }

    @Test
    void deleteAIKnowledgeDocumentTest() {
        //create ai-knowledge-document and assign to ai-context
        var idContext = "context-22-222"; //from testdata - already asigned one ai-document to this ai-context
        var aiKnowledgeDocumentDto = new CreateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("document-ref-id-delete");
        aiKnowledgeDocumentDto.setName("document-ref-name-delete");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.EMBEDDED);

        //get ai-knowledge-document by ai-context
        var documents = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(documents).isNotNull();
        assertThat(documents).isNotNull().isNotEmpty().hasSize(1);

        //create knowledge-document
        var createdDocument = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", idContext)
                .body(aiKnowledgeDocumentDto)
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(createdDocument).isNotNull();
        assertThat(createdDocument.getName()).isEqualTo(aiKnowledgeDocumentDto.getName());
        assertThat(createdDocument.getDocumentRefId()).isEqualTo(aiKnowledgeDocumentDto.getDocumentRefId());
        assertThat(createdDocument.getStatus()).isEqualTo(aiKnowledgeDocumentDto.getStatus());

        //get ai-knowledge-document by ai-context again and check list of documents
        documents = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(documents).isNotNull();
        assertThat(documents).isNotNull().isNotEmpty().hasSize(2);

        //delete ai-knowledge-document
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", createdDocument.getId())
                .delete("/ai-knowledge-documents/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if ai-knowledge-document exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", createdDocument.getId())
                .get("/ai-knowledge-documents/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //get ai-knowledge-document by ai-context again and check list of documents
        documents = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(documents).isNotNull();
        assertThat(documents).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void updateAIKnowledgeDocumentTest() {
        var aiKnowledgeDocumentDto = new UpdateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("updated-document-ref-id");
        aiKnowledgeDocumentDto.setName("updated-document-name");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.EMBEDDED);

        //update not existing ai-knowledge-document
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeDocumentDto)
                .when()
                .pathParam("id", "non-existing-id")
                .put("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //update ai-knowledge-document
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeDocumentDto)
                .when()
                .pathParam("id", "document-22-222")
                .put("/ai-knowledge-documents/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated ai-knowledge-document
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "document-22-222")
                .get("/ai-knowledge-documents/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("document-22-222");
        assertThat(dto.getName()).isEqualTo(aiKnowledgeDocumentDto.getName());
        assertThat(dto.getDocumentRefId()).isEqualTo(aiKnowledgeDocumentDto.getDocumentRefId());
        assertThat(dto.getStatus()).isEqualTo(aiKnowledgeDocumentDto.getStatus());
    }
}
