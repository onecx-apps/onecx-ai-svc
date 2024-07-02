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
@TestHTTPEndpoint(AIContextRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
public class AIContextRestControllerTenantTest extends AbstractTest {

    @Test
    void createAIContextTest() {
        // create aiContext
        var aiContextDto = new CreateAIContextRequestDTO();
        aiContextDto.setName("Context");
        aiContextDto.setLlmSystemMessage("LLm-system-message");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-kb-11-111")
                .body(aiContextDto)
                .post("/ai-knowledgebases/{id}/ai-contexts")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIContextDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-contexts/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-contexts/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIContextDTO.class);

        assertThat(dto).isNotNull()
                .returns(aiContextDto.getName(), from(AIContextDTO::getName))
                .returns(aiContextDto.getLlmSystemMessage(), from(AIContextDTO::getLlmSystemMessage));

        // create aiContext without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", "t-kb-11-111")
                .post("/ai-knowledgebases/{id}/ai-contexts")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createAIContext.createAIContextRequestDTO: must not be null");
    }

}
