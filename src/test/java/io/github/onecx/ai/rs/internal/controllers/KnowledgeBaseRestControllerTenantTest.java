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
import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeBaseDTO;
import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeBaseRequestDTO;
import gen.io.github.onecx.ai.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(KnowlegeBaseRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class KnowledgeBaseRestControllerTenantTest extends AbstractTest {

    @Test
    void createKBTest() {

        // create kb
        var kbDto = new CreateAIKnowledgeBaseRequestDTO();
        kbDto.setAppId("appId");
        kbDto.setName("KnowledgeBase");
        kbDto.setDescription("KnowledgeBaseDescription");

        var dto = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .body(kbDto)
                .post("/ai-knowledgebases")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org2"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledgebases/{id}")
                .then()
                .statusCode(NOT_FOUND.getStatusCode());

        dto = given()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .pathParam("id", dto.getId())
                .get("/ai-knowledgebases/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .extract()
                .body().as(AIKnowledgeBaseDTO.class);

        assertThat(dto).isNotNull()
                .returns(kbDto.getName(), from(AIKnowledgeBaseDTO::getName))
                .returns(kbDto.getDescription(), from(AIKnowledgeBaseDTO::getDescription))
                .returns(kbDto.getAppId(), from(AIKnowledgeBaseDTO::getAppId));

        // create kb without body
        var exception = given()
                .when()
                .contentType(APPLICATION_JSON)
                .header(APM_HEADER_PARAM, createToken("org1"))
                .post("/ai-knowledgebases")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract().as(ProblemDetailResponseDTO.class);

        assertThat(exception.getErrorCode()).isEqualTo("CONSTRAINT_VIOLATIONS");
        assertThat(exception.getDetail()).isEqualTo("createAIKnowledgeBase.createAIKnowledgeBaseRequestDTO: must not be null");

    }

}
