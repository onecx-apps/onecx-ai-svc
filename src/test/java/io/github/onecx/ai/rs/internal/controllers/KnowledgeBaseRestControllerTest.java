package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;

import org.junit.jupiter.api.Test;

import gen.io.github.onecx.ai.rs.internal.model.AIKnowledgeBaseDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(KnowlegeBaseRestController.class)
public class KnowledgeBaseRestControllerTest {

    @Test
    public void createAIKnowledgeBase() {

        //crateAIKnowledgeBase
        AIKnowledgeBaseDTO aiKnowledgeBaseDTO = new AIKnowledgeBaseDTO();
        aiKnowledgeBaseDTO.setVersion(1);
        aiKnowledgeBaseDTO.setId("20");
        aiKnowledgeBaseDTO.setDescription("descriptionValue");
        aiKnowledgeBaseDTO.setName("nameValue");
        aiKnowledgeBaseDTO.setAppId("appId-10");

        given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(aiKnowledgeBaseDTO)
                .post("/ai-knowledgebases")
                .then()
                .statusCode(CREATED.getStatusCode());

    }

}
