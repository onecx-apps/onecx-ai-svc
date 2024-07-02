package io.github.onecx.ai.rs.internal.controllers;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.*;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.tkit.quarkus.test.WithDBData;

import gen.io.github.onecx.ai.rs.internal.model.*;
import io.github.onecx.ai.test.AbstractTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@TestHTTPEndpoint(AIProviderRestController.class)
@WithDBData(value = "data/testdata-internal.xml", deleteBeforeInsert = true, deleteAfterTest = true, rinseAndRepeat = true)
class AIProviderRestControllerTest extends AbstractTest {

    @Test
    void createAIProviderTest() {
        // create provider
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
                .extract()
                .body().as(AIProviderDTO.class);

        assertThat(id).isNotNull();
        assertThat(id.getAppId()).isEqualTo(providerDto.getAppId());
        assertThat(id.getName()).isEqualTo(providerDto.getName());
        assertThat(id.getModelName()).isEqualTo(providerDto.getModelName());
        assertThat(id.getModelVersion()).isEqualTo(providerDto.getModelVersion());
    }

    @Test
    void findAIProviderBySearchCriteriaTest() {
        var criteria = new AIProviderSearchCriteriaDTO();

        var data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIProviderPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(3);
        assertThat(data.getStream()).isNotNull().hasSize(3);

        criteria.setName("provider2");
        data = given()
                .contentType(APPLICATION_JSON)
                .body(criteria)
                .post("/search")
                .then()
                .statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .as(AIProviderPageResultDTO.class);

        assertThat(data).isNotNull();
        assertThat(data.getTotalElements()).isEqualTo(1);
        assertThat(data.getStream()).isNotNull().hasSize(1);
    }

    @Test
    void getAIProviderByIdTest() {
        //provider none exists
        given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "provider-none-exists-id")
                .get("/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //provider exists
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "provider-11-111")
                .get("/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIProviderDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("provider-11-111");
        assertThat(dto.getName()).isEqualTo("provider1");
        assertThat(dto.getDescription()).isEqualTo("provider_description_1");
        assertThat(dto.getLlmUrl()).isEqualTo("http://some.url.org");
        assertThat(dto.getModelName()).isEqualTo("model1");
        assertThat(dto.getModelVersion()).isEqualTo("version1");
        assertThat(dto.getAppId()).isEqualTo("appId");

    }

    @Test
    void deleteAIProviderTest() {
        //get provider and check if exists
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "provider-DELETE_1")
                .get("/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIProviderDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("provider-DELETE_1");

        //delete provider
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "provider-DELETE_1")
                .delete("/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //check if provider exists
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", "provider-DELETE_1")
                .get("/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());
    }

    @Test
    void updateAIProviderTest() {

        var providerDto = new UpdateAIProviderRequestDTO();
        providerDto.setAppId("updated-appId");
        providerDto.setName("updated-Provider");
        providerDto.setModelName("updated-ModelName");
        providerDto.setModelVersion("updated-ModelVersion");

        //update none existing provider
        given()
                .contentType(APPLICATION_JSON)
                .body(providerDto)
                .when()
                .pathParam("id", "does-not-exists")
                .put("/{id}")
                .then().statusCode(NOT_FOUND.getStatusCode());

        //update provider
        given()
                .contentType(APPLICATION_JSON)
                .body(providerDto)
                .when()
                .pathParam("id", "provider-11-111")
                .put("/{id}")
                .then().statusCode(NO_CONTENT.getStatusCode());

        //get updated provider
        var dto = given().contentType(APPLICATION_JSON)
                .when()
                .pathParam("id", "provider-11-111")
                .get("/{id}")
                .then().statusCode(OK.getStatusCode())
                .contentType(APPLICATION_JSON)
                .extract()
                .body().as(AIProviderDTO.class);

        assertThat(dto).isNotNull();
        assertThat(dto.getAppId()).isEqualTo(providerDto.getAppId());
        assertThat(dto.getName()).isEqualTo(providerDto.getName());
        assertThat(dto.getModelVersion()).isEqualTo(providerDto.getModelVersion());
        assertThat(dto.getModelName()).isEqualTo(providerDto.getModelName());
    }
}
