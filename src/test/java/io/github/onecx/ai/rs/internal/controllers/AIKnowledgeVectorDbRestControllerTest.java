package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AIKnowledgeVectorDbRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeVectorDbRestControllerTest extends AbstractTest {

    @Test
    void createKnowledgeVectorDbSuccessfullyTest() {
        var aiKnowledgeVectorDbDto = new CreateAIKnowledgeVectorDbRequestDTO();
        aiKnowledgeVectorDbDto.setName("vector-name");
        aiKnowledgeVectorDbDto.setDescription("vector-description");
        aiKnowledgeVectorDbDto.setPwd("vector-pwd");
        aiKnowledgeVectorDbDto.setUser("user1");
        aiKnowledgeVectorDbDto.setVdbUrl("vdb-url-test");
        aiKnowledgeVectorDbDto.setVdbCollection("collection-test");

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

        //create knowledge-vector-db
        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", contextDto.getId())
                .body(aiKnowledgeVectorDbDto)
                .post("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(aiKnowledgeVectorDbDto.getName());
        assertThat(response.getDescription()).isEqualTo(aiKnowledgeVectorDbDto.getDescription());
        assertThat(response.getVdbUrl()).isEqualTo(aiKnowledgeVectorDbDto.getVdbUrl());
        assertThat(response.getVdbCollection()).isEqualTo(aiKnowledgeVectorDbDto.getVdbCollection());
    }

    @Test
    void createAIKnowledgeVectorDbWithoutAIContextThrowsExceptionTest() {
        var aiKnowledgeVectorDbDto = new CreateAIKnowledgeVectorDbRequestDTO();
        aiKnowledgeVectorDbDto.setName("vector-name");
        aiKnowledgeVectorDbDto.setDescription("vector-description");
        aiKnowledgeVectorDbDto.setPwd("vector-pwd");
        aiKnowledgeVectorDbDto.setUser("user1");
        aiKnowledgeVectorDbDto.setVdbUrl("vdb-url-test");
        aiKnowledgeVectorDbDto.setVdbCollection("collection-test");

        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-ai-context-id")
                .body(aiKnowledgeVectorDbDto)
                .post("/ai-contexts/{id}/ai-knowledge-vdb")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("AI_CONTEXT_DOES_NOT_EXIST", exception.getErrorCode());
        Assertions.assertEquals("AIContext does not exist", exception.getDetail());
    }

    @Test
    void deleteAIKnowledgeVectorDbTest() {
        //delete ai-knowledge-vector-db
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "vector-DELETE_1")
                .delete("/ai-knowledge-vdbs/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if ai-knowledge-vector-db exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "vector-DELETE_1")
                .get("/ai-knowledge-vdbs/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

    }

    @Test
    void getAIKnowledgeVectorDbTest() {
        //get non existing ai-knowledge-vector-db
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-id")
                .get("/ai-knowledge-vdbs/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //get existing ai-knowledge-vector-db
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "vector-11-111")
                .get("/ai-knowledge-vdbs/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo("vector1");
        assertThat(dto.getId()).isEqualTo("vector-11-111");
        assertThat(dto.getDescription()).isEqualTo("vector_description_1");
        assertThat(dto.getVdbUrl()).isEqualTo("vector_vdb_1");
        assertThat(dto.getVdbCollection()).isEqualTo("vector_collection_1");
    }

    @Test
    void updateAIKnowledgeVectorDbTest() {
        var aiKnowledgeVectorDbDto = new CreateAIKnowledgeVectorDbRequestDTO();
        aiKnowledgeVectorDbDto.setName("update-name");
        aiKnowledgeVectorDbDto.setDescription("update-description");
        aiKnowledgeVectorDbDto.setPwd("update-pwd");
        aiKnowledgeVectorDbDto.setUser("update-user1");
        aiKnowledgeVectorDbDto.setVdbUrl("update-url");
        aiKnowledgeVectorDbDto.setVdbCollection("update-collection");

        //update not existing ai-knowledge-vector-db
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeVectorDbDto)
                .when()
                .pathParam("id", "non-existing-ai-context-id")
                .put("/ai-knowledge-vdbs/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //update ai-knowledge-vector-db
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeVectorDbDto)
                .when()
                .pathParam("id", "vector-22-222")
                .put("/ai-knowledge-vdbs/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated ai-knowledge-vector-db
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "vector-22-222")
                .get("/ai-knowledge-vdbs/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeVectorDbDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("vector-22-222");
        assertThat(dto.getName()).isEqualTo("update-name");
        assertThat(dto.getDescription()).isEqualTo("update-description");
        assertThat(dto.getVdbUrl()).isEqualTo("update-url");
        assertThat(dto.getVdbCollection()).isEqualTo("update-collection");
        assertThat(dto.getUser()).isEqualTo("update-user1");
    }
}
