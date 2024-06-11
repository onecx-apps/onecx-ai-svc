package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.CreateAIKnowledgeBaseRequestDTO;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

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
                .extract();

        assertThat(kbId).isNotNull();

    }

}
