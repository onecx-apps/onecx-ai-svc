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
@TestHTTPEndpoint(AIKnowledgeUrlRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeUrlRestControllerTest extends AbstractTest {

    @Test
    void createAIKnowledgeUrlSuccessfullyTest() {
        var aiKnowledgeUrlDto = new CreateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("custom-url");
        aiKnowledgeUrlDto.setDescription("custom-description");
        aiKnowledgeUrlDto.setName("custom-name");

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

        //create knowledge-url
        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", contextDto.getId())
                .body(aiKnowledgeUrlDto)
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(aiKnowledgeUrlDto.getName());
        assertThat(response.getDescription()).isEqualTo(aiKnowledgeUrlDto.getDescription());
        assertThat(response.getUrl()).isEqualTo(aiKnowledgeUrlDto.getUrl());

        //get ai-knowledge-urls by ai-context
        var urlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "context-11-111")
                .get("/ai-contexts/{id}/ai-knowledge-urls")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeUrlDTO>>() {
                });

        assertThat(urlList).isNotNull();
        assertThat(urlList).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void createAIKnowledgeUrlWithoutAIContextThrowsExceptionTest() {
        var aiKnowledgeUrlDto = new CreateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("custom-url");
        aiKnowledgeUrlDto.setDescription("custom-description");
        aiKnowledgeUrlDto.setName("custom-name");

        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-ai-context-id")
                .body(aiKnowledgeUrlDto)
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("AI_CONTEXT_DOES_NOT_EXIST", exception.getErrorCode());
        Assertions.assertEquals("AIContext does not exist", exception.getDetail());
    }

    @Test
    void deleteAIKnowledgeUrlTest() {
        //create ai-knowledge-url and assign to ai-context
        var idContext = "context-22-222"; //from testdata
        var aiKnowledgeUrlDto = new CreateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("custom-url");
        aiKnowledgeUrlDto.setDescription("custom-description");
        aiKnowledgeUrlDto.setName("custom-name");

        //get ai-knowledge-urls by ai-context
        var urlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-urls")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeUrlDTO>>() {
                });

        assertThat(urlList).isNotNull();
        assertThat(urlList).hasSize(0);

        //create ai-knowledge-url
        var urlDtoResponse = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", idContext)
                .body(aiKnowledgeUrlDto)
                .post("/ai-contexts/{id}/ai-knowledge-urls")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(urlDtoResponse).isNotNull();
        assertThat(urlDtoResponse.getName()).isEqualTo(aiKnowledgeUrlDto.getName());
        assertThat(urlDtoResponse.getDescription()).isEqualTo(aiKnowledgeUrlDto.getDescription());
        assertThat(urlDtoResponse.getUrl()).isEqualTo(aiKnowledgeUrlDto.getUrl());

        //get ai-knowledge-urls by ai-context
        urlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-urls")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeUrlDTO>>() {
                });

        assertThat(urlList).isNotNull();
        assertThat(urlList).isNotNull().isNotEmpty().hasSize(1);

        //delete ai-knowledge-url
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", urlDtoResponse.getId())
                .delete("/ai-knowledge-urls/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if ai-knowledge-url exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", urlDtoResponse.getId())
                .get("/ai-knowledge-urls/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //get ai-knowledge-urls by ai-context
        urlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-urls")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeUrlDTO>>() {
                });

        assertThat(urlList).isNotNull();
        assertThat(urlList).hasSize(0);
    }

    @Test
    void getAIKnowledgeUrlTest() {
        //get non-existing ai-knowledge-url
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-id")
                .get("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //get existing ai-knowledge-url
        var dto = given()
                .contentType(APPLICATION_JSON)
                .when().pathParam("id", "url-11-111")
                .get("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("url-11-111");
        assertThat(dto.getName()).isEqualTo("knowledge_url_name1");
        assertThat(dto.getDescription()).isEqualTo("url_description_1");
        assertThat(dto.getUrl()).isEqualTo("url1");
    }

    @Test
    void updateAIKnowledgeUrlTest() {
        var aiKnowledgeUrlDto = new UpdateAIKnowledgeUrlRequestDTO();
        aiKnowledgeUrlDto.setUrl("update-url");
        aiKnowledgeUrlDto.setDescription("update-description");
        aiKnowledgeUrlDto.setName("update-name");

        //update not existing ai-knowledge-url
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeUrlDto)
                .when()
                .pathParam("id", "non-existing-ai-context-id")
                .put("/ai-knowledge-urls/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //update ai-knowledge-url
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeUrlDto)
                .when()
                .pathParam("id", "url-22-222")
                .put("/ai-knowledge-urls/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated ai-knowledge-url
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "url-22-222")
                .get("/ai-knowledge-urls/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeUrlDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("url-22-222");
        assertThat(dto.getName()).isEqualTo("update-name");
        assertThat(dto.getDescription()).isEqualTo("update-description");
        assertThat(dto.getUrl()).isEqualTo("update-url");
    }

}
