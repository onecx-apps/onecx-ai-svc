package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusTest
@TestHTTPEndpoint(KnowlegeBaseRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class KnowledgeBaseRestControllerTest extends AbstractTest {

    @Test
    void createKnowledgebaseTest() {
        // create kb
        var kbDto = new CreateAIKnowledgeBaseRequestDTO();
        kbDto.setAppId("appId");
        kbDto.setName("KnowledgeBase");
        kbDto.setDescription("KnowledgeBaseDescription");

        var kbId = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(kbDto)
                .post("/ai-knowledgebases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(kbId).isNotNull();
        assertThat(kbId.getAppId()).isEqualTo(kbDto.getAppId());
        assertThat(kbId.getName()).isEqualTo(kbDto.getName());
        assertThat(kbId.getDescription()).isEqualTo(kbDto.getDescription());

    }

    @Test
    void deleteKnowledgebaseTest() {
        //delete knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "kb-DELETE_1")
                .delete("/ai-knowledgebases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if knowledgebase exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "kb-DELETE_1")
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void deleteKnowledgebaseShouldDeleteAssignedAIContextTest() {
        //create knowledgebase
        var kbDto = new CreateAIKnowledgeBaseRequestDTO();
        kbDto.setAppId("appId");
        kbDto.setName("KnowledgeBase-custom");
        kbDto.setDescription("KnowledgeBaseDescription");

        var responseKbDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(kbDto)
                .post("/ai-knowledgebases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(responseKbDto).isNotNull();
        assertThat(responseKbDto.getId()).isNotNull();

        //create assigned ai-context
        var aiContextDto = new CreateAIContextRequestDTO();
        aiContextDto.setName("name-value");

        var responseAiContextDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .body(aiContextDto)
                .post("/ai-knowledgebases/{id}/ai-contexts")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(responseAiContextDto).isNotNull();

        //check if knowledgebase has ai-context
        var contexts = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseKbDto.getId())
                .get("/ai-knowledgebases/{id}/ai-contexts")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIContextDTO>>() {
                });

        assertThat(contexts).isNotNull();
        assertThat(contexts).isNotNull().isNotEmpty().hasSize(1);

        //delete knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .delete("/ai-knowledgebases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if knowledgebase exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //check if ai-context exists
        given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateKnowledgebaseTest() {

        var kbDto = new UpdateAIKnowledgeBaseRequestDTO();
        kbDto.setAppId("custom-name-app-id");
        kbDto.setName("custom-name");
        kbDto.setDescription("updated-value-description");

        //update none existing knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .body(kbDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("/ai-knowledgebases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //update knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .body(kbDto)
                .when()
                .pathParam("id", "kb-11-111")
                .put("/ai-knowledgebases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated knowledgebase
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "kb-11-111")
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo(kbDto.getAppId());
        assertThat(dto.getName()).isEqualTo(kbDto.getName());
        assertThat(dto.getDescription()).isEqualTo(kbDto.getDescription());
    }

    @Test
    void getKnowledgebaseByIdTest() {
        //knowledgebase none exists
        given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "kb-none-exists-id")
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //knowledgebase exists
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "kb-11-111")
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("kb-11-111");
    }

    @Test
    void findAIKnowledgeBasesBySearchCriteriaTest() {
        var criteria = new AIKnowledgeBaseSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/ai-knowledgebases/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeBasePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName("knowledgebase2");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/ai-knowledgebases/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIKnowledgeBasePageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);
    }

    @Test
    void deleteKnowledgebaseCascadeTest() {
        //test case - create knowledge base with assigned aicontext with assigned vectordb, db, url, document
        //then delete kb and expected to be deleted cascade all connected elements

        /*****************************************
         * CREATION PART OF TEST
         *****************************************/

        //create kb
        var kbDto = new CreateAIKnowledgeBaseRequestDTO();
        kbDto.setAppId("cascade-app-id");
        kbDto.setName("cascade-kb");
        kbDto.setDescription("cascade-description");

        var responseKbDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(kbDto)
                .post("/ai-knowledgebases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(responseKbDto).isNotNull();
        assertThat(responseKbDto.getId()).isNotNull();

        //create aiContext
        var aiContextDto = new CreateAIContextRequestDTO();
        aiContextDto.setName("cascade-name-context");

        var responseAiContextDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .body(aiContextDto)
                .post("/ai-knowledgebases/{id}/ai-contexts")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(responseAiContextDto).isNotNull();
        assertThat(responseAiContextDto.getId()).isNotNull();

        //create knowledge-document
        var aiKnowledgeDocumentDto = new CreateAIKnowledgeDocumentRequestDTO();
        aiKnowledgeDocumentDto.setDocumentRefId("document-ref-id");
        aiKnowledgeDocumentDto.setName("document-ref-name");
        aiKnowledgeDocumentDto.setStatus(DocumentStatusTypeDTO.NEW);

        var responseDocumentDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseAiContextDto.getId())
                .body(aiKnowledgeDocumentDto)
                .post("/ai-contexts/{id}/ai-knowledge-documents")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDocumentDTO.class);

        assertThat(responseDocumentDto).isNotNull();
        assertThat(responseDocumentDto.getId()).isNotNull();

        //create vectordb
        var aiKnowledgeVectorDbDto = new CreateAIKnowledgeVectorDbRequestDTO();
        aiKnowledgeVectorDbDto.setName("vector-name");
        aiKnowledgeVectorDbDto.setDescription("vector-description");
        aiKnowledgeVectorDbDto.setPwd("vector-pwd");
        aiKnowledgeVectorDbDto.setUser("user1");
        aiKnowledgeVectorDbDto.setVdbUrl("vdb-url-test");
        aiKnowledgeVectorDbDto.setVdbCollection("collection-test");

        var responseVectorDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseAiContextDto.getId())
                .body(aiKnowledgeVectorDbDto)
                .post("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(responseVectorDto).isNotNull();
        assertThat(responseVectorDto.getId()).isNotNull();

        //create knowledge-url
        var aiKnowledgeUrlDto = new CreateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("custom-url");
        aiKnowledgeUrlDto.setDescription("custom-description");
        aiKnowledgeUrlDto.setName("custom-name");

        //create knowledge-url
        var responseUrlDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseAiContextDto.getId())
                .body(aiKnowledgeUrlDto)
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(responseUrlDto).isNotNull();
        assertThat(responseUrlDto.getId()).isNotNull();

        //create knowledge-db
        var aiKnowledgeDatabaseDto = new CreateAIKnowledgeDatabaseRequestDTO();
        List<String> tables = new ArrayList<>();
        tables.add("table-db");

        aiKnowledgeDatabaseDto.setDescription("db-description");
        aiKnowledgeDatabaseDto.setName("db-name");
        aiKnowledgeDatabaseDto.setDb("db-1");
        aiKnowledgeDatabaseDto.setUser("db-user");
        aiKnowledgeDatabaseDto.setPwd("pdw-db");
        aiKnowledgeDatabaseDto.setTables(tables);

        var responseDatabaseDto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseAiContextDto.getId())
                .body(aiKnowledgeDatabaseDto)
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(responseDatabaseDto).isNotNull();
        assertThat(responseDatabaseDto.getId()).isNotNull();

        /*****************************************
         * CHECKING ASSIGNATION PART OF TEST
         *****************************************/

        //check if knowledgebase has ai-context
        var contexts = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseKbDto.getId())
                .get("/ai-knowledgebases/{id}/ai-contexts")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIContextDTO>>() {
                });

        assertThat(contexts).isNotNull();
        assertThat(contexts).isNotNull().isNotEmpty().hasSize(1);

        //check if ai-context has knowledge-document
        var knowledgeDocumentList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}/ai-knowledge-documents")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDocumentDTO>>() {
                });

        assertThat(knowledgeDocumentList).isNotNull();
        assertThat(knowledgeDocumentList).isNotNull().isNotEmpty().hasSize(1);

        //check if ai-context has vector-db
        var knowledgeVectorDbDto = given()
                .contentType(APPLICATION_JSON)
                .when().pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(knowledgeVectorDbDto).isNotNull();
        assertThat(knowledgeVectorDbDto.getId()).isNotNull();

        //check if ai-context has knowledge-url
        var knowledgeUrlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}/ai-knowledge-urls")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeUrlDTO>>() {
                });

        assertThat(knowledgeUrlList).isNotNull();
        assertThat(knowledgeUrlList).isNotNull().isNotEmpty().hasSize(1);

        //check if ai-context has knowledge-database
        var knowledgeDatabaseList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}/ai-knowledge-databases")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDatabaseDTO>>() {
                });

        assertThat(knowledgeDatabaseList).isNotNull();
        assertThat(knowledgeDatabaseList).isNotNull().isNotEmpty().hasSize(1);

        /*****************************************
         * DELETE KNOWLEDGEBASE
         * AND CHECKING IF ENTITIES EXISTS
         *****************************************/

        //delete knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .delete("/ai-knowledgebases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //expected not found on knowledgebase
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseKbDto.getId())
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //expected not found on ai-context
        given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", responseAiContextDto.getId())
                .get("/ai-contexts/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //expected not found on knowledge-document
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseDocumentDto.getId())
                .get("/ai-knowledge-documents/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //expected not found on vector-db
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseVectorDto.getId())
                .get("/ai-knowledge-vdbs/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //expected not found on knowledge-url
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseUrlDto.getId())
                .get("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //expected not found on knowledge-database
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", responseDatabaseDto.getId())
                .get("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());
    }
}
