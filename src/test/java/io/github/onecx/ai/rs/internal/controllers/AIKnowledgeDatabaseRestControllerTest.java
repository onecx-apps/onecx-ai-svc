package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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
@TestHTTPEndpoint(AIKnowledgeDatabaseRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIKnowledgeDatabaseRestControllerTest extends AbstractTest {

    @Test
    void createAIKnowledgeUrlSuccessfullyTest() {
        var aiKnowledgeDatabaseDto = new CreateAIKnowledgeDatabaseRequestDTO();
        List<String> tables = new ArrayList<>();
        tables.add("table-db");

        aiKnowledgeDatabaseDto.setDescription("db-description");
        aiKnowledgeDatabaseDto.setName("db-name");
        aiKnowledgeDatabaseDto.setDb("db-1");
        aiKnowledgeDatabaseDto.setUser("db-user");
        aiKnowledgeDatabaseDto.setPwd("pdw-db");
        aiKnowledgeDatabaseDto.setTables(tables);

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

        //create knowledge-database
        var response = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", contextDto.getId())
                .body(aiKnowledgeDatabaseDto)
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(aiKnowledgeDatabaseDto.getName());
        assertThat(response.getDescription()).isEqualTo(aiKnowledgeDatabaseDto.getDescription());
        assertThat(response.getDb()).isEqualTo(aiKnowledgeDatabaseDto.getDb());
        assertThat(response.getUser()).isEqualTo(aiKnowledgeDatabaseDto.getUser());

        //get ai-knowledge-url by ai-context
        var urlList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "context-11-111")
                .get("/ai-contexts/{id}/ai-knowledge-databases")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDatabaseDTO>>() {
                });

        assertThat(urlList).isNotNull();
        assertThat(urlList).isNotNull().isNotEmpty().hasSize(1);
    }

    @Test
    void createAIKnowledgeDatabaseWithoutAIContextThrowsExceptionTest() {
        var aiKnowledgeDatabaseDto = new CreateAIKnowledgeDatabaseRequestDTO();
        List<String> tables = new ArrayList<>();
        tables.add("table-db");

        aiKnowledgeDatabaseDto.setDescription("db-description");
        aiKnowledgeDatabaseDto.setName("db-name");
        aiKnowledgeDatabaseDto.setDb("db-1");
        aiKnowledgeDatabaseDto.setUser("db-user");
        aiKnowledgeDatabaseDto.setPwd("pdw-db");
        aiKnowledgeDatabaseDto.setTables(tables);

        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-ai-context-id")
                .body(aiKnowledgeDatabaseDto)
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("AI_CONTEXT_DOES_NOT_EXIST", exception.getErrorCode());
        Assertions.assertEquals("AIContext does not exist", exception.getDetail());
    }

    @Test
    void deleteAIKnowledgeDatabaseTest() {
        //create ai-knowledge-database and assign to ai-context
        var idContext = "context-22-222"; //from testdata
        var aiKnowledgeDatabaseDto = new CreateAIKnowledgeDatabaseRequestDTO();
        List<String> tables = new ArrayList<>();
        tables.add("table-db");

        aiKnowledgeDatabaseDto.setDescription("db-description");
        aiKnowledgeDatabaseDto.setName("db-name");
        aiKnowledgeDatabaseDto.setDb("db-1");
        aiKnowledgeDatabaseDto.setUser("db-user");
        aiKnowledgeDatabaseDto.setPwd("pdw-db");
        aiKnowledgeDatabaseDto.setTables(tables);

        //get ai-knowledge-databases by ai-context
        var databaseList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-databases")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDatabaseDTO>>() {
                });

        assertThat(databaseList).isNotNull();
        assertThat(databaseList).hasSize(0);

        //create ai-knowledge-database
        var databaseDtoResponse = given()
                .when()
                .contentType(APPLICATION_JSON)
                .pathParam("id", idContext)
                .body(aiKnowledgeDatabaseDto)
                .post("/ai-contexts/{id}/ai-knowledge-databases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(databaseDtoResponse).isNotNull();
        assertThat(databaseDtoResponse.getName()).isEqualTo(aiKnowledgeDatabaseDto.getName());
        assertThat(databaseDtoResponse.getDescription()).isEqualTo(aiKnowledgeDatabaseDto.getDescription());
        assertThat(databaseDtoResponse.getDb()).isEqualTo(aiKnowledgeDatabaseDto.getDb());
        assertThat(databaseDtoResponse.getUser()).isEqualTo(aiKnowledgeDatabaseDto.getUser());

        //get ai-knowledge-databases by ai-context
        databaseList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-databases")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDatabaseDTO>>() {
                });

        assertThat(databaseList).isNotNull();
        assertThat(databaseList).isNotNull().isNotEmpty().hasSize(1);

        //delete ai-knowledge-database
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", databaseDtoResponse.getId())
                .delete("/ai-knowledge-databases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if ai-knowledge-database exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", databaseDtoResponse.getId())
                .get("/ai-knowledge-databases/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //get ai-knowledge-databases by ai-context
        databaseList = given()
                .contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", idContext)
                .get("/ai-contexts/{id}/ai-knowledge-databases")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(new TypeRef<List<AIKnowledgeDatabaseDTO>>() {
                });

        assertThat(databaseList).isNotNull();
        assertThat(databaseList).hasSize(0);
    }

    @Test
    void getAIKnowledgeUrlTest() {
        //get non existing ai-knowledge-database
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "non-existing-id")
                .get("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //get existing ai-knowledge-database
        var dto = given()
                .contentType(APPLICATION_JSON)
                .when().pathParam("id", "db-11-111")
                .get("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("db-11-111");
        assertThat(dto.getName()).isEqualTo("knowledge_db_name1");
        assertThat(dto.getDescription()).isEqualTo("db_description_1");
        assertThat(dto.getDb()).isEqualTo("db1");
        assertThat(dto.getUser()).isEqualTo("usr1");
    }

    @Test
    void updateAIKnowledgeUrlTest() {
        var aiKnowledgeDatabaseDto = new UpdateAIKnowledgeDatabaseRequestDTO();
        List<String> tables = new ArrayList<>();
        tables.add("table-db");

        aiKnowledgeDatabaseDto.setDescription("updated-db-description");
        aiKnowledgeDatabaseDto.setName("updated-db-name");
        aiKnowledgeDatabaseDto.setDb("updated-db-1");
        aiKnowledgeDatabaseDto.setUser("updated-db-user");
        aiKnowledgeDatabaseDto.setPwd("updated-pdw-db");
        aiKnowledgeDatabaseDto.setTables(tables);

        //update not existing ai-knowledge-database
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeDatabaseDto)
                .when()
                .pathParam("id", "non-existing-ai-context-id")
                .put("/ai-knowledge-databases/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        //update ai-knowledge-database
        given()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeDatabaseDto)
                .when()
                .pathParam("id", "db-22-222")
                .put("/ai-knowledge-databases/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated ai-knowledge-database
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "db-22-222")
                .get("/ai-knowledge-databases/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIKnowledgeDatabaseDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("db-22-222");
        assertThat(dto.getName()).isEqualTo("updated-db-name");
        assertThat(dto.getDescription()).isEqualTo("updated-db-description");
        assertThat(dto.getDb()).isEqualTo("updated-db-1");
    }
}
