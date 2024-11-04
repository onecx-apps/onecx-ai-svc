package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;

@QuarkusTest
@TestHTTPEndpoint(AIContextRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIContextRestControllerTest extends AbstractTest {

    @Test
    void createAIContextWithoutKnowledgeBase() {
        //create aiContext
        var aiContextDto = new CreateAIContextRequestDTO();
        aiContextDto.setName("name");

        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(aiContextDto)
                .post("/ai-knowledgebases/notexistingkbid/ai-contexts")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    void createAIContext() {
        //create aiContext with kb
        var aiContextDto = new CreateAIContextRequestDTO();
        aiContextDto.setName("name-value");

        //get knowledge base
        var kbDto = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "kb-22-222")
                .get("/ai-knowledgebases/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(kbDto).isNotNull();

        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", kbDto.getId())
                .body(aiContextDto)
                .post("/ai-knowledgebases/{id}/ai-contexts")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(response).isNotNull();
        //check if context has kbid
    }

    @Test
    void deleteAIContexTest() {
        //delete ai-context
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "context-DELETE_1")
                .delete("/ai-contexts/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if ai-context exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "context-DELETE_1")
                .get("/ai-contexts/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateAIContextTest() {
        var contextDto = new UpdateAIContextRequestDTO();
        contextDto.setName("custom-name-app-id");

        //update none existing ai context
        given()
                .contentType(APPLICATION_JSON)
                .body(contextDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("/ai-contexts/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //update ai-context
        given()
                .contentType(APPLICATION_JSON)
                .body(contextDto)
                .when()
                .pathParam("id", "context-11-111")
                .put("/ai-contexts/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get ai-context and check updated value
        var dto = given()
                .contentType(APPLICATION_JSON)
                .when().pathParam("id", "context-11-111")
                .get("/ai-contexts/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("context-11-111");
        assertThat(dto.getName()).isEqualTo(contextDto.getName());

    }

    @Test
    void getAIContextsByKnowledgeBaseIdTest() {
        //get contexts by knowledge-base
        var contexts = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "kb-11-111")
                .get("/ai-knowledgebases/{id}/ai-contexts")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIContextDTO>>() {
                });

        assertThat(contexts).isNotNull();
        assertThat(contexts).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void getAIContextTest() {
        //guid="context-11-111"
        //name="context1"
        //llm_system_message="context_llm_system_message_1

        //try to get ai-context with non-existing id
        given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "non-existing-ai-context-id")
                .get("/ai-contexts/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //get ai-context success
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
        assertThat(contextDto.getId()).isEqualTo("context-11-111");
        assertThat(contextDto.getName()).isEqualTo("context1");
    }

    @Test
    void findAIContextBySearchCriteriaTest() {
        var criteria = new AIContextSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/ai-contexts/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIContextPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName("context2");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/ai-contexts/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIContextPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);

    }
}
