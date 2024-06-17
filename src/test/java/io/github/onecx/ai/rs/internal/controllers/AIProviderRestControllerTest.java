package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.CreateAIProviderRequestDTO;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AIProviderRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class AIProviderRestControllerTest extends AbstractTest {

    @Test
    void createAIProviderTest() {
        // create kb
        var providerDto = new CreateAIProviderRequestDTO();
        providerDto.setAppId("appId");
        providerDto.setName("Provider");
        providerDto.setModelName("ModelName");
        providerDto.setModelVersion("ModelVersion");

        var id = given()
                .when()
                .contentType(APPLICATION_JSON)
                .body(providerDto)
                .post()
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract();

        assertThat(id).isNotNull();

    }

}
